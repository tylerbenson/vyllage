package accounts.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import accounts.email.EmailBuilder;
import accounts.model.BatchAccount;
import accounts.model.CSRFToken;
import accounts.model.Organization;
import accounts.model.OrganizationMember;
import accounts.model.Role;
import accounts.model.User;
import accounts.model.UserRole;
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.AccountSettingRepository;
import accounts.repository.ElementNotFoundException;
import accounts.repository.OrganizationMemberRepository;
import accounts.repository.OrganizationRepository;
import accounts.repository.OrganizationRoleRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;
import accounts.repository.UserRoleRepository;
import accounts.validation.EmailValidator;

@Service
public class UserService {
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	@Autowired
	private DocumentService documentService;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganizationRoleRepository organizationRoleRepository;

	@Autowired
	private OrganizationMemberRepository organizationMemberRepository;

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private AccountSettingRepository settingRepository;

	@Autowired
	private RandomPasswordGenerator randomPasswordGenerator;

	@Autowired
	private EmailBuilder emailBuilder;

	@Autowired
	private Environment env;

	public User getUser(String username) {
		return this.userRepository.loadUserByUsername(username);
	}

	public List<User> getAllUsers() {
		return this.userRepository.getAll();
	}

	public boolean userExists(String userName) {
		return this.userRepository.userExists(userName);
	}

	public void batchCreateUsers(BatchAccount batchAccount)
			throws IllegalArgumentException, EmailException {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Assert.notNull(batchAccount.getOrganization());
		Assert.notNull(batchAccount.getRole());
		Assert.notNull(batchAccount.getEmails());

		String[] emailSplit = batchAccount.getEmails()
				.replace(";", System.lineSeparator())
				.replace(",", System.lineSeparator()).trim()
				.split(System.lineSeparator());

		boolean invalid = Arrays.stream(emailSplit).map(String::trim)
				.map(EmailValidator::validate).collect(Collectors.toList())
				.stream().anyMatch(p -> p.booleanValue() != true);

		if (invalid)
			throw new IllegalArgumentException(
					"Contains invalid email addresses.");

		Role role = roleRepository.get(batchAccount.getRole());

		// note, for organization member and role there's no userId until it's
		// saved.
		List<User> users = Arrays
				.stream(emailSplit)
				.map(String::trim)
				.map(s -> new User(s, randomPasswordGenerator
						.getRandomPassword(), enabled, accountNonExpired,
						credentialsNonExpired, accountNonLocked, Arrays
								.asList(new UserRole(role.getRole(), null)),
						Arrays.asList(new OrganizationMember(batchAccount
								.getOrganization(), null))))
				.collect(Collectors.toList());

		userRepository.saveUsers(users);

		// send mails
		for (User user : users) {
			emailBuilder
					.to(user.getUsername())
					.from(env.getProperty("email.userName",
							"no-reply@vyllage.com"))
					.subject("Account Creation - Vyllage.com")
					.setNoHtmlMessage(
							"Your account has been created successfuly. \\n Your password is: "
									+ user.getPassword())
					.templateName("email-account-created")
					.addTemplateVariable("password", user.getPassword()).send();
		}
	}

	/**
	 * Process a link request, creates new user with a random password.
	 * 
	 * @param linkRequest
	 * @return link response
	 * @throws EmailException
	 */
	public User createUser(DocumentLinkRequest linkRequest)
			throws EmailException {
		boolean invalid = false;

		if (EmailValidator.validate(linkRequest.getEmail()) == invalid)
			throw new IllegalArgumentException(
					"Contains invalid email address.");

		// assigns current user's Organizations
		// assigns default role.
		String randomPassword = randomPasswordGenerator.getRandomPassword();

		List<UserRole> defaultAuthoritiesForNewUser = userRoleRepository
				.getDefaultAuthoritiesForNewUser();

		User user = new User(null, linkRequest.getFirstName(), null,
				linkRequest.getLastName(), linkRequest.getEmail(),
				randomPassword, true, true, true, true,
				defaultAuthoritiesForNewUser,
				((User) SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal()).getOrganizationMember(), null, null);
		userRepository.createUser(user);

		User loadUserByUsername = userRepository.loadUserByUsername(linkRequest
				.getEmail());

		if (linkRequest.sendRegistrationMail()) {
			// send mail

			emailBuilder
					.to(linkRequest.getEmail())
					.from(env.getProperty("email.userName",
							"no-reply@vyllage.com"))
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
	 * Updates the user data, roles and organizations. DOES NOT CHANGE USER
	 * PASSWORD.
	 */
	public void update(User user) {
		userRepository.updateUser(user);
	}

	public List<AccountSetting> getAccountSettings(User user) {
		return settingRepository.getAccountSettings(user);
	}

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

		// set organizations
		savedSettings.addAll(setOrganizations(
				user,
				settings.stream()
						.filter(set -> set.getName().equalsIgnoreCase(
								"organization")).collect(Collectors.toList())));

		// set roles
		savedSettings.addAll(setRoles(
				user,
				settings.stream()
						.filter(set -> set.getName().equalsIgnoreCase("role"))
						.collect(Collectors.toList())));

		return settings;
	}

	private List<AccountSetting> setRoles(User user,
			List<AccountSetting> newRoles) {

		// create new role relationship
		List<UserRole> userRoles = newRoles.stream()
				.map(set -> new UserRole(set.getValue(), user.getUserId()))
				.collect(Collectors.toList());

		// save user roles
		// authorities are immutable in Spring's user details.

		User user2 = new User(user.getUserId(), user.getFirstName(),
				user.getMiddleName(), user.getLastName(), user.getUsername(),
				user.getPassword(), user.isEnabled(),
				user.isAccountNonExpired(), user.isCredentialsNonExpired(),
				user.isAccountNonLocked(), userRoles,
				user.getOrganizationMember(), user.getDateCreated(),
				user.getLastModified());

		this.update(user2);

		// delete current roles, they will be replaced with the new
		// ones.
		settingRepository.deleteByName(user.getUserId(), "role");

		// saving and returning the new settings
		return newRoles.stream().map(set -> setAccountSetting(user, set))
				.collect(Collectors.toList());
	}

	protected List<AccountSetting> setOrganizations(User user,
			List<AccountSetting> newOrganizations) {

		// create new organization relationship
		List<OrganizationMember> organizationMembers = newOrganizations
				.stream()
				.map(set -> new OrganizationMember(organizationRepository
						.getByName(set.getValue()).getOrganizationId(), user
						.getUserId())).collect(Collectors.toList());

		// save user organizations
		user.setOrganizationMember(organizationMembers);

		this.update(user);

		// delete current organizations, they will be replaced with the new
		// ones.
		settingRepository.deleteByName(user.getUserId(), "organization");

		// saving and returning the new settings
		return newOrganizations.stream()
				.map(set -> setAccountSetting(user, set))
				.collect(Collectors.toList());
	}

	/**
	 * Returns the list of organizations a given user belongs too.
	 * 
	 * @param user
	 * @return
	 */
	protected List<Organization> getOrganizationsForUser(User user) {
		return organizationMemberRepository.getByUserId(user.getUserId())
				.stream()
				.map(om -> organizationRepository.get(om.getOrganizationId()))
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
					user.getAuthorities(), user.getOrganizationMember(),
					user.getDateCreated(), user.getLastModified());
			this.update(newUser);
		}

	}

	public void changePassword(String newPassword) {
		userRepository.changePassword(newPassword);
	}

	/**
	 * Returns account contact information
	 */
	public List<AccountContact> getAccountContactForUsers(List<Long> userIds) {
		List<AccountSetting> accountSettings = settingRepository
				.getAccountSettings(userIds);

		if (accountSettings == null || accountSettings.isEmpty())
			return Arrays.asList();

		Map<Long, List<AccountSetting>> map = accountSettings.stream().collect(
				Collectors.groupingBy((AccountSetting as) -> as.getUserId(),
						Collectors.mapping((AccountSetting as) -> as,
								Collectors.toList())));

		return map.entrySet().stream().map(UserService::mapAccountContact)
				.collect(Collectors.toList());
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
}
