package accounts.service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.model.BatchAccount;
import accounts.model.CSRFToken;
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.AccountSettingRepository;
import accounts.repository.ElementNotFoundException;
import accounts.repository.OrganizationRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;
import accounts.repository.UserOrganizationRoleRepository;
import accounts.service.aspects.CheckPrivacy;
import accounts.service.utilities.BatchParser;
import accounts.service.utilities.BatchParser.ParsedAccount;
import accounts.service.utilities.RandomPasswordGenerator;
import accounts.validation.EmailValidator;

import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

@Service
public class UserService {
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	@Autowired
	private DocumentService documentService;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private AccountSettingRepository settingRepository;

	@Autowired
	private RandomPasswordGenerator randomPasswordGenerator;

	@Autowired
	@Qualifier(value = "accounts.emailBuilder")
	private EmailBuilder emailBuilder;

	@Autowired
	private Environment env;

	private BatchParser batchParser = new BatchParser();

	public User getUser(String username) {
		return this.userRepository.loadUserByUsername(username);
	}

	public List<User> getAllUsers() {
		return this.userRepository.getAll();
	}

	public boolean userExists(String userName) {
		return this.userRepository.userExists(userName);
	}

	public void batchCreateUsers(BatchAccount batchAccount, User loggedInUser)
			throws IllegalArgumentException, EmailException, IOException {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Assert.notNull(batchAccount.getOrganization());
		Assert.notNull(batchAccount.getRole());
		Assert.notNull(batchAccount.getTxt());

		List<ParsedAccount> parsedAccounts = batchParser.parse(batchAccount
				.getTxt());

		// note, for UserOrganizationRole there's no userId until it's
		// saved.
		List<User> users = parsedAccounts
				.stream()
				.map(pa -> new User(null, pa.getFirstName(),
						pa.getMiddleName(), pa.getLastName(), pa.getEmail(),
						randomPasswordGenerator.getRandomPassword(), enabled,
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, Arrays
								.asList(new UserOrganizationRole(null,
										batchAccount.getOrganization(),
										batchAccount.getRole(), loggedInUser
												.getUserId())), null, null))
				.collect(Collectors.toList());

		// find existing GUEST users to activate
		// https://github.com/natebenson/vyllage/issues/502
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			User existingUser = null;

			try {
				existingUser = userRepository.loadUserByUsername(user
						.getUsername());
			} catch (Exception e) {
				// continue.
			}

			if (existingUser != null && existingUser.isGuest()) {
				String newPassword = randomPasswordGenerator
						.getRandomPassword();

				this.changePassword(existingUser.getUserId(), newPassword);

				List<GrantedAuthority> newRolesForOrganization = new ArrayList<>(
						existingUser.getAuthorities());
				newRolesForOrganization.add(new UserOrganizationRole(
						existingUser.getUserId(), batchAccount
								.getOrganization(), batchAccount.getRole(),
						loggedInUser.getUserId()));

				updateUserRolesByOrganization(newRolesForOrganization,
						loggedInUser);

				User newUpdateUser = new User(existingUser.getUserId(),
						user.getFirstName(), user.getMiddleName(),
						user.getLastName(), existingUser.getUsername(),
						newPassword, existingUser.isEnabled(),
						existingUser.isAccountNonExpired(),
						existingUser.isCredentialsNonExpired(),
						existingUser.isAccountNonLocked(),
						newRolesForOrganization, existingUser.getDateCreated(),
						existingUser.getLastModified());

				this.update(newUpdateUser);

				emailBuilder
						.to(existingUser.getUsername())
						.from(env.getProperty("email.from",
								"no-reply@vyllage.com"))
						.fromUserName(
								env.getProperty("email.from.userName",
										"Chief of Vyllage"))
						.subject("Account Creation - Vyllage.com")
						.setNoHtmlMessage(
								"Your account has been created successfuly. \\n Your password is: "
										+ user.getPassword())
						.templateName("email-account-created")
						.addTemplateVariable("password", newPassword)
						.addTemplateVariable("firstName", user.getFirstName())
						.send();

				// remove the user from the batch
				iterator.remove();
			}
		}

		// find existing users to update instead of save/activate
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			User existingUser = null;

			try {
				existingUser = userRepository.loadUserByUsername(user
						.getUsername());
			} catch (Exception e) {
				// continue.
			}

			if (existingUser != null) {
				// change roles
				Long userId = existingUser.getUserId();

				List<GrantedAuthority> newRolesForOrganization = new ArrayList<>();

				newRolesForOrganization.addAll(user.getAuthorities());
				newRolesForOrganization.stream().forEach(
						ga -> ((UserOrganizationRole) ga).setUserId(userId));

				// remove duplicates
				// newRolesForOrganization
				// .removeAll(existingUser.getAuthorities());
				// newRolesForOrganization.addAll(existingUser.getAuthorities());

				updateUserRolesByOrganization(newRolesForOrganization,
						loggedInUser);

				existingUser.setFirstName(user.getFirstName());
				existingUser.setMiddleName(user.getMiddleName());
				existingUser.setLastName(user.getLastName());
				this.update(existingUser);

				// remove the user from the batch
				iterator.remove();
			}
		}

		userRepository.saveUsers(users, loggedInUser);

		// send mails
		for (User user : users) {
			sendAccountCreationEmail(user);
		}
	}

	protected void sendAccountCreationEmail(User user) throws EmailException {
		emailBuilder
				.to(user.getUsername())
				.from(env.getProperty("email.from", "no-reply@vyllage.com"))
				.fromUserName(
						env.getProperty("email.from.userName",
								"Chief of Vyllage"))
				.subject("Account Creation - Vyllage.com")
				.setNoHtmlMessage(
						"Your account has been created successfuly. \\n Your password is: "
								+ user.getPassword())
				.templateName("email-account-created")
				.addTemplateVariable("password", user.getPassword())
				.addTemplateVariable("firstName", user.getFirstName()).send();
	}

	/**
	 * Process a link request, creates new user with a random password.
	 * 
	 * @param linkRequest
	 * @return link response
	 * @throws EmailException
	 */
	public User createUser(DocumentLinkRequest linkRequest, User loggedInUser)
			throws EmailException {
		boolean invalid = false;

		if (EmailValidator.isValid(linkRequest.getEmail()) == invalid)
			throw new IllegalArgumentException(
					"Contains invalid email address.");

		// assigns current user's Organizations
		// assigns default Guest role.
		String randomPassword = randomPasswordGenerator.getRandomPassword();

		List<GrantedAuthority> loggedUseRoles = new ArrayList<>();
		List<UserOrganizationRole> defaultAuthoritiesForNewUser = new ArrayList<>();

		loggedUseRoles.addAll(((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getAuthorities());

		// setting up organizations and roles for the user account, we set the
		// same organizations the logged in user belongs to and assign the Guest
		// Role, user id is null until saved
		for (GrantedAuthority userOrganizationRole : loggedUseRoles)
			defaultAuthoritiesForNewUser.add(new UserOrganizationRole(null,
					((UserOrganizationRole) userOrganizationRole)
							.getOrganizationId(), RolesEnum.GUEST.name(),
					loggedInUser.getUserId()));

		User user = new User(null, linkRequest.getFirstName(), null,
				linkRequest.getLastName(), linkRequest.getEmail(),
				randomPassword, true, true, true, true,
				defaultAuthoritiesForNewUser, null, null);
		userRepository.createUser(user);

		User loadUserByUsername = userRepository.loadUserByUsername(linkRequest
				.getEmail());

		if (linkRequest.sendRegistrationMail()) {
			// send mail

			emailBuilder
					.to(linkRequest.getEmail())
					.from(env.getProperty("email.from", "no-reply@vyllage.com"))
					.fromUserName(
							env.getProperty("email.from.userName",
									"Chief of Vyllage"))
					.subject("Account Creation")
					.setNoHtmlMessage(
							"Your account has been created successfuly. \\n Your password is: "
									+ randomPassword)
					.templateName("email-account-created")
					.addTemplateVariable("password", randomPassword).send();
		}

		return loadUserByUsername;
	}

	public User getUser(Long userId) throws UserNotFoundException {
		return userRepository.get(userId);
	}

	public List<User> getAdvisors(User loggedUser, Map<String, String> filters,
			int maxsize) {
		return userRepository.getAdvisors(loggedUser, filters, maxsize);
	}

	public List<AccountNames> getNames(List<Long> userIds) {
		return userRepository.getNames(userIds);
	}

	/**
	 * Updates the user data. DOES NOT CHANGE USER PASSWORD.
	 */
	public void update(User user) {
		userRepository.updateUser(user);
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSettings(List<Long> userIds) {
		return settingRepository.getAccountSettings(userIds);
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSettings(User user) {
		return settingRepository.getAccountSettings(user);
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSetting(final User user,
			final String settingName) throws ElementNotFoundException {
		assert settingName != null;

		return settingRepository.get(user.getUserId(), settingName);
	}

	public AccountSetting setAccountSetting(final User user,
			AccountSetting setting) {

		if (setting.getUserId() == null)
			setting.setUserId(user.getUserId());

		switch (setting.getName()) {
		case "firstName":
			setFirstName(user, setting);
			return settingRepository.set(user.getUserId(), setting);

		case "middleName":
			setMiddleName(user, setting);
			return settingRepository.set(user.getUserId(), setting);

		case "lastName":
			setLastName(user, setting);
			return settingRepository.set(user.getUserId(), setting);

		case "email":
			setEmail(user, setting);
			return settingRepository.set(user.getUserId(), setting);
		default:
			return settingRepository.set(user.getUserId(), setting);
		}
	}

	public List<AccountSetting> setAccountSettings(final User user,
			List<AccountSetting> settings) {

		List<AccountSetting> savedSettings = new ArrayList<>();

		savedSettings.addAll(settings
				.stream()
				.filter(set -> !set.getName().equalsIgnoreCase("role")
						|| !set.getName().equalsIgnoreCase("organization"))
				.map(set -> setAccountSetting(user, set))
				.collect(Collectors.toList()));

		return savedSettings;
	}

	public void updateUserRolesByOrganization(
			List<GrantedAuthority> newRolesForOrganization, User loggedInUser) {

		// Long organizationId = ((UserOrganizationRole) newRolesForOrganization
		// .get(0)).getOrganizationId();
		// Long userId = ((UserOrganizationRole) newRolesForOrganization.get(0))
		// .getUserId();

		// delete all the roles related to the user in the organization
		// userOrganizationRoleRepository.deleteByUserIdAndOrganizationId(userId,
		// organizationId);

		for (GrantedAuthority userOrganizationRole : newRolesForOrganization) {
			((UserOrganizationRole) userOrganizationRole)
					.setAuditUserId(loggedInUser.getUserId());
			userOrganizationRoleRepository
					.create((UserOrganizationRole) userOrganizationRole);
		}

	}

	/**
	 * Returns the list of organizations a given user belongs too.
	 * 
	 * @param user
	 * @return
	 */
	public List<Organization> getOrganizationsForUser(User user) {
		return user
				.getAuthorities()
				.stream()
				.map(om -> organizationRepository
						.get(((UserOrganizationRole) om).getOrganizationId()))
				.collect(Collectors.toList());
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setFirstName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setFirstName(setting.getValue());
			this.update(user);
		}
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setMiddleName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setMiddleName(setting.getValue());
			this.update(user);
		}
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setLastName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setLastName(setting.getValue());
			this.update(user);
		}
	}

	// changes the username and email...
	protected void setEmail(User user, AccountSetting setting) {
		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			// username is final...
			// password is erased after the user logins but we need something
			// here, even if we won't change it
			User newUser = new User(user.getUserId(), user.getFirstName(),
					user.getMiddleName(), user.getLastName(),
					setting.getValue(), "a password we don't care about",
					user.isEnabled(), user.isAccountNonExpired(),
					user.isCredentialsNonExpired(), user.isAccountNonLocked(),
					user.getAuthorities(), user.getDateCreated(),
					user.getLastModified());
			this.update(newUser);
		}

	}

	public void changePassword(Long userId, String newPassword) {
		userRepository.updateCredential(newPassword, userId);
	}

	public void changePassword(String newPassword) {
		userRepository.changePassword(newPassword);
	}

	/**
	 * Returns account contact information
	 */
	public List<AccountContact> getAccountContactForUsers(
			List<AccountSetting> accountSettings) {

		if (accountSettings == null || accountSettings.isEmpty())
			return Arrays.asList();

		Map<Long, List<AccountSetting>> map = accountSettings.stream().collect(
				Collectors.groupingBy((AccountSetting as) -> as.getUserId(),
						Collectors.mapping((AccountSetting as) -> as,
								Collectors.toList())));

		return map
				.entrySet()
				.stream()
				.map(UserService::mapAccountContact)
				.map(ac -> {
					// hmmm, this should never happen...
					try {
						ac.setRegisteredOn(this.getUser(ac.getUserId())
								.getDateCreated().toInstant(ZoneOffset.UTC)
								.getEpochSecond());
					} catch (UserNotFoundException e) {
						logger.severe(ExceptionUtils.getStackTrace(e));
						NewRelic.noticeError(e);
					}
					return ac;
				}).collect(Collectors.toList());
	}

	/**
	 * Maps a list of contact related account settings into a contact object.
	 * 
	 * @param entry
	 * @return
	 */
	protected static AccountContact mapAccountContact(
			Entry<Long, List<AccountSetting>> entry) {
		AccountContact ac = new AccountContact();

		Long userId = entry.getKey();
		Optional<AccountSetting> address = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("address"))
				.findFirst();
		Optional<AccountSetting> email = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("email"))
				.findFirst();
		Optional<AccountSetting> phoneNumber = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("phoneNumber"))
				.findFirst();
		Optional<AccountSetting> twitter = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("twitter"))
				.findFirst();
		Optional<AccountSetting> linkedIn = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("linkedIn"))
				.findFirst();

		Optional<AccountSetting> firstName = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("firstName"))
				.findFirst();

		Optional<AccountSetting> middleName = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("middleName"))
				.findFirst();

		Optional<AccountSetting> lastName = entry.getValue().stream()
				.filter(as -> as.getName().equalsIgnoreCase("lastName"))
				.findFirst();

		ac.setUserId(userId);

		if (address.isPresent())
			ac.setAddress(address.get().getValue());

		if (email.isPresent())
			ac.setEmail(email.get().getValue());

		if (phoneNumber.isPresent())
			ac.setPhoneNumber(phoneNumber.get().getValue());

		if (twitter.isPresent())
			ac.setTwitter(twitter.get().getValue());

		if (linkedIn.isPresent())
			ac.setLinkedIn(linkedIn.get().getValue());

		if (firstName.isPresent())
			ac.setFirstName(firstName.get().getValue());

		if (middleName.isPresent())
			ac.setMiddleName(middleName.get().getValue());

		if (lastName.isPresent())
			ac.setLastName(lastName.get().getValue());

		return ac;
	}

	/**
	 * Deletes a user and logs him out of the system.
	 * 
	 * @param request
	 * 
	 * @param userId
	 * @param token
	 * @throws ServletException
	 * @throws UserNotFoundException
	 */
	public void delete(HttpServletRequest request, Long userId, CSRFToken token)
			throws ServletException, UserNotFoundException {
		User user = userRepository.get(userId);

		logger.info("Disabling user.");
		userRepository.deleteUser(user.getUsername());

		logger.info("Deleting user documents.");
		documentService.deleteUsers(request, Arrays.asList(userId), token);

		logger.info("Have a nice day.");
		request.logout(); // good bye :)

	}

	public List<User> getUsers(List<Long> userIds) {

		return userRepository.getAll(userIds);
	}

	public void setEmailBuilder(EmailBuilder emailBuilder) {
		this.emailBuilder = emailBuilder;
	}

	public User createUser(String email, String firstName, String middleName,
			String lastName, Long auditUserId) {

		if (auditUserId == null)
			throw new AccessDeniedException(
					"Account creation is not allowed without a referring user.");

		String randomPassword = randomPasswordGenerator.getRandomPassword();
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User auditUser = null;
		try {
			auditUser = this.getUser(auditUserId);
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// add similar organization
		List<UserOrganizationRole> defaultAuthoritiesForNewUser = new ArrayList<>();

		for (GrantedAuthority userOrganizationRole : auditUser.getAuthorities())
			defaultAuthoritiesForNewUser.add(new UserOrganizationRole(null,
					((UserOrganizationRole) userOrganizationRole)
							.getOrganizationId(), RolesEnum.GUEST.name(),
					auditUser.getUserId()));

		User user = new User(null, firstName, middleName, lastName, email,
				randomPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				defaultAuthoritiesForNewUser, null, null);

		userRepository.createUser(user);

		return user;
	}
}
