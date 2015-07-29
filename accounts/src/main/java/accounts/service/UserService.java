package accounts.service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.OrganizationEnum;
import user.common.constants.RolesEnum;
import accounts.model.BatchAccount;
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.ChangeEmailLink;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.AvatarSourceEnum;
import accounts.model.account.settings.Privacy;
import accounts.model.form.RegisterForm;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.AvatarRepository;
import accounts.repository.ElementNotFoundException;
import accounts.repository.OrganizationRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;
import accounts.repository.UserOrganizationRoleRepository;
import accounts.service.utilities.BatchParser;
import accounts.service.utilities.BatchParser.ParsedAccount;
import accounts.service.utilities.RandomPasswordGenerator;
import accounts.validation.EmailValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

@Service
public class UserService {
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	private static final String GRAVATAR_URL = "https://secure.gravatar.com/avatar/";

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
	private RandomPasswordGenerator randomPasswordGenerator;

	@Autowired
	@Qualifier(value = "accounts.emailBuilder")
	private EmailBuilder emailBuilder;

	@Autowired
	private Environment environment;

	@Autowired
	@Qualifier(value = "accounts.ExecutorService")
	private ExecutorService executorService;

	@Autowired
	private AvatarRepository avatarRepository;

	@Autowired
	private AccountSettingsService accountSettingsService;

	@Autowired
	private DSLContext sql;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TextEncryptor encryptor;

	@Autowired
	private SignInUtil signInUtil;

	private BatchParser batchParser = new BatchParser();

	public User getUser(Long userId) throws UserNotFoundException {
		return userRepository.get(userId);
	}

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

				List<UserOrganizationRole> newRolesForOrganization = new ArrayList<>();
				for (GrantedAuthority grantedAuthority : existingUser
						.getAuthorities())
					newRolesForOrganization
							.add((UserOrganizationRole) grantedAuthority);

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
						.from(environment.getProperty("email.from",
								"no-reply@vyllage.com"))
						.fromUserName(
								environment.getProperty("email.from.userName",
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

				List<UserOrganizationRole> newRolesForOrganization = new ArrayList<>();
				for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
					newRolesForOrganization
							.add((UserOrganizationRole) grantedAuthority);
				}

				newRolesForOrganization.stream().forEach(
						ga -> ga.setUserId(userId));

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

		userRepository.addUsers(users, loggedInUser);

		// send mails
		for (User user : users) {
			sendAutomatedAccountCreationEmail(user.getUsername(),
					user.getPassword(), user.getFirstName());
		}
	}

	protected void sendAutomatedAccountCreationEmail(String email,
			String password, String firstName) throws EmailException {
		emailBuilder
				.to(email)
				.from(environment.getProperty("email.from",
						"no-reply@vyllage.com"))
				.fromUserName(
						environment.getProperty("email.from.userName",
								"Chief of Vyllage"))
				.subject("Account Creation - Vyllage.com")
				.setNoHtmlMessage(
						"Your account has been created successfuly. \\n Your password is: "
								+ password)
				.templateName("email-account-created")
				.addTemplateVariable("password", password)
				.addTemplateVariable("firstName", firstName).send();
	}

	protected void sendUserRegisteredEmail(String email, String password,
			String firstName) throws EmailException {
		emailBuilder
				.to(email)
				.from(environment.getProperty("email.from",
						"no-reply@vyllage.com"))
				.fromUserName(
						environment.getProperty("email.from.userName",
								"Chief of Vyllage"))
				.subject("Account Creation - Vyllage.com")
				.setNoHtmlMessage(
						"Your account has been created successfuly. \\n Your password is: "
								+ password)
				.templateName("email-user-registered")
				.addTemplateVariable("password", password)
				.addTemplateVariable("firstName", firstName).send();
	}

	protected void updateUserRolesByOrganization(
			List<UserOrganizationRole> newRolesForOrganization,
			User loggedInUser) {

		// Long organizationId = ((UserOrganizationRole) newRolesForOrganization
		// .get(0)).getOrganizationId();
		// Long userId = ((UserOrganizationRole) newRolesForOrganization.get(0))
		// .getUserId();

		// delete all the roles related to the user in the organization
		// userOrganizationRoleRepository.deleteByUserIdAndOrganizationId(userId,
		// organizationId);

		for (UserOrganizationRole userOrganizationRole : newRolesForOrganization) {
			userOrganizationRole.setAuditUserId(loggedInUser.getUserId());
			userOrganizationRoleRepository.create(userOrganizationRole);
		}

	}

	/**
	 * Returns names of the requested user ids.
	 * 
	 * @param userIds
	 * @return
	 */
	public List<AccountNames> getNames(List<Long> userIds) {
		return userRepository.getNames(userIds);
	}

	/**
	 * Updates the user data. DOES NOT CHANGE USER PASSWORD.
	 */
	public void update(User user) {
		userRepository.updateUser(user);
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

		return map.entrySet().stream().map(UserService::mapAccountContact)
				.map(setUserRegisteredOn()).map(addAvatarUrl())
				.collect(Collectors.toList());
	}

	private Function<? super AccountContact, ? extends AccountContact> addAvatarUrl() {
		return ac -> {

			try {
				ac.setAvatarUrl(getAvatar(ac.getUserId()));
			} catch (UserNotFoundException e) {
				// this should never happen since we found them previously
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
			return ac;

		};
	}

	private Function<? super AccountContact, ? extends AccountContact> setUserRegisteredOn() {
		return ac -> {
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
		};
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
	public void delete(HttpServletRequest request, Long userId)
			throws ServletException, UserNotFoundException {
		User user = userRepository.get(userId);

		logger.info("Disabling user.");
		userRepository.deleteUser(user.getUsername());

		logger.info("Deleting user documents.");
		documentService.deleteUsers(request, Arrays.asList(userId));

		logger.info("Have a nice day.");
		request.logout(); // good bye :)

	}

	public List<User> getUsers(List<Long> userIds) {
		return userRepository.getAll(userIds);
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

		createReceiveAdviceSetting(false, loadUserByUsername);

		return loadUserByUsername;
	}

	/**
	 * Creates a user using a registration form.
	 * 
	 * @param registerForm
	 * @return user
	 * @throws EmailException
	 */
	public User createUser(RegisterForm registerForm) {

		boolean isEnabled = true;
		boolean isAccountNonExpired = true;
		boolean isCredentialsNonExpired = true;
		boolean isAccountNonLocked = true;

		// defaulting to Guest Role and Guest Organization

		Long auditUserId = Long.valueOf(0L);// main admin.

		UserOrganizationRole uor = new UserOrganizationRole(null,
				OrganizationEnum.GUESTS.getOrganizationId(), RolesEnum.GUEST
						.name().toUpperCase(), auditUserId);

		List<UserOrganizationRole> newRolesForOrganization = Arrays.asList(uor);

		User newUser = new User(null, registerForm.getFirstName(), null,
				registerForm.getLastName(), registerForm.getEmail(),
				registerForm.getPassword(), isEnabled, isAccountNonExpired,
				isCredentialsNonExpired, isAccountNonLocked,
				newRolesForOrganization, null, null);

		logger.info(newUser.toString());

		this.userRepository.createUser(newUser);
		// get id
		newUser = this.getUser(registerForm.getEmail());

		createReceiveAdviceSetting(registerForm.getReceiveAdvice(), newUser);

		try {
			sendUserRegisteredEmail(newUser.getUsername(),
					registerForm.getPassword(), newUser.getFirstName());
		} catch (EmailException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return newUser;
	}

	/**
	 * Creates a user using a registration form with a referring user.
	 * 
	 * @param registerForm
	 * @param auditUserId
	 * @return User
	 */
	public User createUserFromReferral(RegisterForm registerForm,
			Long auditUserId) {

		if (auditUserId == null)
			throw new AccessDeniedException(
					"Account creation is not allowed without a referring user.");

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User auditUser = null;
		try {
			auditUser = this.getUser(auditUserId);
		} catch (UserNotFoundException e) {
			// should never happen
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		// add similar organization
		List<UserOrganizationRole> defaultAuthoritiesForNewUser = new ArrayList<>();

		for (GrantedAuthority userOrganizationRole : auditUser.getAuthorities())
			defaultAuthoritiesForNewUser.add(new UserOrganizationRole(null,
					((UserOrganizationRole) userOrganizationRole)
							.getOrganizationId(), RolesEnum.GUEST.name(),
					auditUser.getUserId()));

		User user = new User(null, registerForm.getFirstName(), null,
				registerForm.getLastName(), registerForm.getEmail(),
				registerForm.getPassword(), enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				defaultAuthoritiesForNewUser, null, null);

		userRepository.createUser(user);
		User newUser = this.getUser(user.getUsername());

		createReceiveAdviceSetting(registerForm.getReceiveAdvice(), newUser);

		try {
			sendUserRegisteredEmail(newUser.getUsername(),
					registerForm.getPassword(), newUser.getFirstName());
		} catch (EmailException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return newUser;
	}

	public List<User> getUsersFromOrganization(Long organizationId) {
		List<UserOrganizationRole> byOrganizationId = userOrganizationRoleRepository
				.getByOrganizationId(organizationId);

		return userRepository.getAll(byOrganizationId.stream()
				.map(uor -> uor.getUserId()).collect(Collectors.toList()));
	}

	public void appendUserOrganizationRoles(
			List<UserOrganizationRole> userOrganizationRole) {
		userOrganizationRole.stream().forEach(
				uor -> userOrganizationRoleRepository.create(uor));
	}

	public void setUserRoles(List<UserOrganizationRole> userOrganizationRoles) {

		userOrganizationRoles
				.stream()
				.collect(
						Collectors.groupingBy(UserOrganizationRole::getUserId,
								Collectors.toList()))
				.forEach(
						(userId, organizations) -> {
							for (UserOrganizationRole userOrganizationRole : organizations) {
								userOrganizationRoleRepository
										.deleteByUserIdAndOrganizationId(
												userId, userOrganizationRole
														.getOrganizationId());
							}
						});

		userOrganizationRoles.stream().forEach(
				uor -> userOrganizationRoleRepository.create(uor));

	}

	public void setUserOrganization(
			List<UserOrganizationRole> userOrganizationRoles) {

		userOrganizationRoleRepository.deleteByUserId(userOrganizationRoles
				.get(0).getUserId());

		userOrganizationRoles.stream().forEach(
				uor -> userOrganizationRoleRepository.create(uor));

	}

	/**
	 * Enables or disables a given user returning the new state.
	 * 
	 * @param userId
	 * @return
	 */
	public boolean enableDisableUser(Long userId) {
		return userRepository.enableDisableUser(userId);
	}

	/**
	 * Activates an user account, the account must be GUEST and have no other
	 * role.
	 * 
	 * @param userId
	 * @throws UserNotFoundException
	 * @throws EmailException
	 */
	public void activateUser(Long userId, Long organizationId, User loggedInUser)
			throws UserNotFoundException {
		User existingUser = this.getUser(userId);

		if (existingUser.isGuest()) {
			String newPassword = randomPasswordGenerator.getRandomPassword();

			// Setting the user as Student.
			this.setUserRoles(Arrays.asList(new UserOrganizationRole(userId,
					organizationId, RolesEnum.STUDENT.name(), loggedInUser
							.getUserId())));

			this.changePassword(existingUser.getUserId(), newPassword);

			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						emailBuilder
								.to(existingUser.getUsername())
								.from(environment.getProperty("email.from",
										"no-reply@vyllage.com"))
								.fromUserName(
										environment.getProperty(
												"email.from.userName",
												"Chief of Vyllage"))
								.subject("Account Creation - Vyllage.com")
								.setNoHtmlMessage(
										"Your account has been created successfuly. \\n Your password is: "
												+ newPassword)
								.templateName("email-account-created")
								.addTemplateVariable("password", newPassword)
								.addTemplateVariable("firstName",
										existingUser.getFirstName()).send();
					} catch (EmailException e) {
						logger.severe(ExceptionUtils.getStackTrace(e));
						NewRelic.noticeError(e);
					}

				}
			});

		}
	}

	/**
	 * Returns the user's avatar based on the user's social networks profile or
	 * avatar setting, if it can't find any returns a gravatar url.
	 * 
	 * @param userId
	 * @return avatar url
	 * @throws UserNotFoundException
	 */
	public String getAvatar(Long userId) throws UserNotFoundException {

		User user = this.getUser(userId);
		List<AccountSetting> avatarSettings = null;
		Optional<AccountSetting> avatarSetting = Optional.empty();

		try {
			avatarSettings = accountSettingsService.getAccountSetting(user,
					"avatar");

			// there's only one
			avatarSetting = Optional.ofNullable(avatarSettings.get(0));

		} catch (ElementNotFoundException e) {
			// not really important
			logger.warning(ExceptionUtils.getStackTrace(e));
		}

		if (avatarSetting.isPresent()
				&& avatarSetting.get().getValue()
						.equalsIgnoreCase(AvatarSourceEnum.GRAVATAR.name()))
			return GRAVATAR_URL
					+ new String(DigestUtils.md5Hex(user.getUsername()));

		else if (avatarSetting.isPresent()) {

			Optional<String> avatarUrl = avatarRepository.getAvatar(userId,
					avatarSetting.get().getValue());

			if (avatarUrl.isPresent())
				return avatarUrl.get();
		}

		// nothing found, defaulting to gravatar
		return GRAVATAR_URL
				+ new String(DigestUtils.md5Hex(user.getUsername()));
	}

	/**
	 * Starts the email address (username) change process. <br>
	 * Sends an email to confirm that the user wanted to change the email, until
	 * the user clicks the confirmation link the email will remain the same.
	 * 
	 * 
	 * @param user
	 * @param email
	 * @throws EmailException
	 * @throws JsonProcessingException
	 */
	public void sendEmailChangeConfirmation(@NonNull User user,
			@NonNull String email) throws EmailException,
			JsonProcessingException {
		Assert.isTrue(!email.isEmpty());

		ChangeEmailLink link = new ChangeEmailLink(user.getUserId(), email);

		String jsonChangeEmailLink = mapper.writeValueAsString(link);

		String encryptedString = encryptor.encrypt(jsonChangeEmailLink);

		String encodedString = Base64.getUrlEncoder().encodeToString(
				encryptedString.getBytes());

		String url = "https://"
				+ environment.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/account/change-email/";

		emailBuilder
				.to(email)
				.from(environment.getProperty("email.from",
						"no-reply@vyllage.com"))
				.fromUserName(
						environment.getProperty("email.from.userName",
								"Chief of Vyllage"))
				.subject("Email Change Confirmation")
				.setNoHtmlMessage(
						"We received a request to change your email if you requested it please copy and paste the link to confirm. \\n"
								+ url + encodedString)
				.templateName("email-change-email-confirmation")
				.addTemplateVariable("userName", user.getFirstName())
				.addTemplateVariable("newEmail", link.getNewEmail())
				.addTemplateVariable("url", url)
				.addTemplateVariable("changeEmail", encodedString).send();
	}

	public void changeEmail(@NonNull User user, @NonNull String email) {
		Assert.isTrue(!email.isEmpty());

		List<AccountSetting> accountSetting;

		try {
			accountSetting = accountSettingsService.getAccountSetting(user,
					"newEmail");
		} catch (ElementNotFoundException e) {
			throw new AccessDeniedException("Invalid link provided.");
		}

		if (!email.equalsIgnoreCase(accountSetting.get(0).getValue()))
			throw new AccessDeniedException("Invalid link provided.");

		userRepository.changeEmail(user, email);

		// to set the new name
		signInUtil.signIn(email);
	}

	public void setEmailBuilder(EmailBuilder emailBuilder) {
		this.emailBuilder = emailBuilder;
	}

	private void createReceiveAdviceSetting(boolean receiveAdvice, User newUser) {

		AccountSetting setting = new AccountSetting(null, newUser.getUserId(),
				"receiveAdvice", String.valueOf(receiveAdvice), Privacy.PRIVATE
						.name().toLowerCase());

		this.accountSettingsService.setAccountSetting(newUser, setting);
	}
}
