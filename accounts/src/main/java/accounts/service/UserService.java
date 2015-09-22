package accounts.service;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
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
import user.common.constants.AccountSettingsEnum;
import user.common.constants.OrganizationEnum;
import user.common.constants.RolesEnum;
import user.common.web.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.ChangeEmailLink;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.AvatarSourceEnum;
import accounts.model.account.settings.Privacy;
import accounts.model.form.RegisterForm;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.AvatarRepository;
import accounts.repository.OrganizationRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;
import accounts.repository.UserOrganizationRoleRepository;
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

	public User getUser(Long userId) throws UserNotFoundException {
		return userRepository.get(userId);
	}

	public User getUser(String username) {
		return this.userRepository.loadUserByUsername(username);
	}

	public boolean userExists(String userName) {
		return this.userRepository.userExists(userName);
	}

	protected void sendUserRegisteredEmail(String email, String password,
			String firstName) {

		Runnable run = () -> {

			try {
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
			} catch (EmailException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		executorService.execute(run);
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

	protected void changePassword(Long userId, String newPassword) {
		userRepository.updateCredential(newPassword, userId);
	}

	public void changePassword(String newPassword) {
		userRepository.changePassword(newPassword);
	}

	public void forcedPasswordChange(Long userId, String userName,
			String newPassword) {
		userRepository.forcedPasswordChange(userId, userName, newPassword);
	}

	/**
	 * Returns account contact information for several users.
	 *
	 * @param request
	 */
	public List<AccountContact> getAccountContacts(HttpServletRequest request,
			@NonNull List<Long> userIds) {

		if (userIds.isEmpty())
			return Collections.emptyList();

		// getting settings
		List<AccountSetting> accountSettings = accountSettingsService
				.getAccountSettings(userIds);

		// mapping settings by user
		Map<Long, List<AccountSetting>> map = accountSettings.stream().collect(
				Collectors.groupingBy((AccountSetting as) -> as.getUserId(),
						Collectors.mapping((AccountSetting as) -> as,
								Collectors.toList())));

		// generating account contact
		List<AccountContact> accountContacts = map.entrySet().stream()
				.map(e -> this.mapAccountContact(e)).map(addAvatarUrl())
				.map(addIsAdvisor()).collect(Collectors.toList());

		// getting taglines
		Map<String, String> taglines = documentService
				.getDocumentHeaderTagline(request, accountContacts.stream()
						.map(ac -> ac.getUserId()).collect(Collectors.toList()));

		// adding taglines to each user
		if (taglines != null && !taglines.isEmpty())
			accountContacts.forEach(ac -> ac.setTagline(taglines.getOrDefault(
					ac.getUserId().toString(), "")));

		return accountContacts;
	}

	private Function<? super AccountContact, ? extends AccountContact> addIsAdvisor() {
		return ac -> {

			boolean isAdvisor = sql.fetchExists(sql
					.select()
					.from(USER_ORGANIZATION_ROLES)
					.where(USER_ORGANIZATION_ROLES.USER_ID.eq(ac.getUserId())
							.and(USER_ORGANIZATION_ROLES.ROLE
									.contains(RolesEnum.ADVISOR.name()))));

			ac.setAdvisor(isAdvisor);

			return ac;
		};
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

	/**
	 * Maps a list of contact related account settings into a contact object.
	 *
	 * @param entry
	 * @return
	 * @throws UserNotFoundException
	 */
	protected AccountContact mapAccountContact(
			Entry<Long, List<AccountSetting>> entry) {
		AccountContact ac = new AccountContact();

		Long userId = entry.getKey();
		Optional<AccountSetting> address = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.address.name())).findFirst();
		Optional<AccountSetting> email = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.email.name())).findFirst();
		Optional<AccountSetting> phoneNumber = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.phoneNumber.name())).findFirst();
		Optional<AccountSetting> twitter = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.twitter.name())).findFirst();
		Optional<AccountSetting> linkedIn = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.linkedIn.name())).findFirst();

		Optional<AccountSetting> firstName = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.firstName.name())).findFirst();

		Optional<AccountSetting> middleName = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.middleName.name())).findFirst();

		Optional<AccountSetting> lastName = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.lastName.name())).findFirst();

		Optional<AccountSetting> siteUrl = entry
				.getValue()
				.stream()
				.filter(as -> as.getName().equalsIgnoreCase(
						AccountSettingsEnum.siteUrl.name())).findFirst();

		ac.setUserId(userId);

		if (address.isPresent())
			ac.setAddress(address.get().getValue());

		if (email.isPresent())
			ac.setEmail(email.get().getValue());
		else
			try {
				ac.setEmail(this.getUser(userId).getUsername());
			} catch (UserNotFoundException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}

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

		if (siteUrl.isPresent())
			ac.setSiteUrl(siteUrl.get().getValue());

		return ac;
	}

	/**
	 * Deletes a user and logs him out of the system.
	 *
	 * @param request
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
					"User: "
							+ loggedInUser.getUsername()
							+ " Link creation error: contains invalid email address, address: "
							+ linkRequest.getEmail());

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

		boolean accountEnabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		User user = new User(null, linkRequest.getFirstName(), null,
				linkRequest.getLastName(), linkRequest.getEmail(),
				randomPassword, accountEnabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				defaultAuthoritiesForNewUser, null, null);

		boolean forcePasswordChange = true;
		userRepository.createUser(user, forcePasswordChange);

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

		boolean forcePasswordChange = false;
		this.userRepository.createUser(newUser, forcePasswordChange);

		// get id
		newUser = this.getUser(registerForm.getEmail());

		createReceiveAdviceSetting(registerForm.getReceiveAdvice(), newUser);

		sendUserRegisteredEmail(newUser.getUsername(),
				registerForm.getPassword(), newUser.getFirstName());

		return newUser;
	}

	/**
	 * Creates a user using a registration form with a referring user.
	 *
	 * @param registerForm
	 * @param auditUserId
	 * @param forcePasswordChange
	 * @return User
	 */
	public User createUserFromReferral(RegisterForm registerForm,
			Long auditUserId, boolean forcePasswordChange) {

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

		userRepository.createUser(user, forcePasswordChange);

		User newUser = this.getUser(user.getUsername());

		createReceiveAdviceSetting(registerForm.getReceiveAdvice(), newUser);

		sendUserRegisteredEmail(newUser.getUsername(),
				registerForm.getPassword(), newUser.getFirstName());
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
		Optional<AccountSetting> avatarSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.avatar.name());

		if (!avatarSetting.isPresent())
			return getDefaultAvatar(user);

		boolean avatarSettingPresent_gravatar = avatarSetting.isPresent()
				&& avatarSetting.get().getValue()
						.equalsIgnoreCase(AvatarSourceEnum.GRAVATAR.name());

		boolean avatarSettingPresent_lti = avatarSetting.isPresent()
				&& avatarSetting.get().getValue()
						.equalsIgnoreCase(AvatarSourceEnum.LTI.name());

		boolean avatarSettingPresent_facebook = avatarSetting.isPresent()
				&& avatarSetting.get().getValue()
						.equalsIgnoreCase(AvatarSourceEnum.FACEBOOK.name());

		if (avatarSettingPresent_gravatar) {
			return getDefaultAvatar(user);
		} else {

			if (avatarSettingPresent_lti) {
				// get avatar url
				Optional<AccountSetting> ltiAvatarUrl = accountSettingsService
						.getAccountSetting(user,
								AccountSettingsEnum.lti_avatar.name());
				if (ltiAvatarUrl.isPresent())
					return ltiAvatarUrl.get().getValue();

			} else {

				// we only have facebook right now
				if (avatarSettingPresent_facebook) {
					// social
					Optional<String> avatarUrl = avatarRepository.getAvatar(
							userId, avatarSetting.get().getValue());

					if (avatarUrl.isPresent())
						return avatarUrl.get();
				}
			}
		}

		// nothing found, defaulting to gravatar
		return getDefaultAvatar(user);
	}

	private String getDefaultAvatar(User user) {
		return GRAVATAR_URL
				+ new String(DigestUtils.md5Hex(user.getUsername()));
	}

	/**
	 * Starts the email address (username) change process. <br>
	 * Sends an email to confirm that the user wanted to change the email, until
	 * the user clicks the confirmation link the email will remain the same.
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

		Runnable run = () -> {

			try {

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
										+ url + "?changeEmail=" + encodedString)
						.templateName("email-change-email-confirmation")
						.addTemplateVariable("userName", user.getFirstName())
						.addTemplateVariable("newEmail", link.getNewEmail())
						.addTemplateVariable("url", url)
						.addTemplateVariable("changeEmail", encodedString)
						.send();
			} catch (EmailException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		executorService.execute(run);
	}

	public void changeEmail(@NonNull User user, @NonNull String email) {
		Assert.isTrue(!email.isEmpty());

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, "newEmail");

		if (!accountSetting.isPresent())
			throw new AccessDeniedException("Invalid link provided.");

		if (!email.equalsIgnoreCase(accountSetting.get().getValue()))
			throw new AccessDeniedException("Invalid link provided.");

		userRepository.changeEmail(user, email);

		// to set the new name
		signInUtil.signIn(email);
	}

	private void createReceiveAdviceSetting(boolean receiveAdvice, User newUser) {

		AccountSetting setting = new AccountSetting(null, newUser.getUserId(),
				"receiveAdvice", String.valueOf(receiveAdvice), Privacy.PRIVATE
						.name().toLowerCase());

		this.accountSettingsService.setAccountSetting(newUser, setting);
	}

}
