package accounts.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
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
import accounts.model.account.AccountNames;
import accounts.model.account.ChangeEmailLink;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.model.form.RegisterForm;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.AccountSettingRepository;
import accounts.repository.OrganizationRepository;
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

	private final DocumentService documentService;

	private final RegistrationEmailService registrationEmailService;

	private final ExecutorService executorService;

	private final OrganizationRepository organizationRepository;

	private final UserOrganizationRoleRepository userOrganizationRoleRepository;

	private final UserDetailRepository userRepository;

	private final AccountSettingRepository accountSettingRepository;

	private final RandomPasswordGenerator randomPasswordGenerator;

	private final EmailBuilder emailBuilder;

	private final Environment environment;

	private final ObjectMapper mapper;

	private final TextEncryptor encryptor;

	@Inject
	private SignInUtil signInUtil;

	@Inject
	public UserService(
			DocumentService documentService,
			RegistrationEmailService registrationEmailService,
			@Qualifier(value = "accounts.ExecutorService") ExecutorService executorService,
			OrganizationRepository organizationRepository,
			UserOrganizationRoleRepository userOrganizationRoleRepository,
			UserDetailRepository userRepository,
			AccountSettingRepository accountSettingRepository,
			RandomPasswordGenerator randomPasswordGenerator,
			@Qualifier(value = "accounts.emailBuilder") EmailBuilder emailBuilder,
			final Environment environment, final ObjectMapper mapper,
			final TextEncryptor encryptor) {
		this.documentService = documentService;
		this.registrationEmailService = registrationEmailService;
		this.executorService = executorService;
		this.organizationRepository = organizationRepository;
		this.userOrganizationRoleRepository = userOrganizationRoleRepository;
		this.userRepository = userRepository;
		this.accountSettingRepository = accountSettingRepository;
		this.randomPasswordGenerator = randomPasswordGenerator;
		this.emailBuilder = emailBuilder;
		this.environment = environment;
		this.mapper = mapper;
		this.encryptor = encryptor;

	}

	public User getUser(Long userId) throws UserNotFoundException {
		return userRepository.get(userId);
	}

	public User getUser(String username) {
		return this.userRepository.loadUserByUsername(username);
	}

	public boolean userExists(String userName) {
		return this.userRepository.userExists(userName);
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

		boolean forcePasswordChange = false;
		boolean sendConfirmationEmail = false;
		User guestUser = userRepository.createUser(user, forcePasswordChange,
				sendConfirmationEmail);

		this.createReceiveAdviceSetting(false, guestUser);

		return guestUser;
	}

	/**
	 * Creates a user using a registration form.
	 *
	 * @param registerForm
	 * @return user
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
		boolean sendConfirmationEmail = true;
		this.userRepository.createUser(newUser, forcePasswordChange,
				sendConfirmationEmail);

		// get id
		newUser = this.getUser(registerForm.getEmail());

		this.createReceiveAdviceSetting(registerForm.getReceiveAdvice(),
				newUser);

		this.registrationEmailService.sendUserRegisteredEmail(
				newUser.getUsername(), registerForm.getPassword(),
				newUser.getFirstName());

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

		boolean sendConfirmationEmail = false;
		User newUser = userRepository.createUser(user, forcePasswordChange,
				sendConfirmationEmail);

		this.createReceiveAdviceSetting(registerForm.getReceiveAdvice(),
				newUser);

		this.registrationEmailService.sendUserRegisteredEmail(
				newUser.getUsername(), registerForm.getPassword(),
				newUser.getFirstName());
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

			this.registrationEmailService.sendAutomatedAccountCreationEmail(
					existingUser.getUsername(), newPassword,
					existingUser.getFirstName());
		}
	}

	/**
	 * Starts the email address (username) change process. <br>
	 * Sends an email to confirm that the user wanted to change the email, until
	 * the user clicks the confirmation link the email will remain the same.
	 *
	 * @param user
	 * @param email
	 * @throws JsonProcessingException
	 */
	public void sendEmailChangeConfirmation(@NonNull User user,
			@NonNull String email) throws JsonProcessingException {
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
								"chief@vyllage.com"))
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
		Assert.isTrue(!StringUtils.isBlank(email));

		Optional<AccountSetting> accountSetting = accountSettingRepository.get(
				user.getUserId(), "newEmail");

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

		accountSettingRepository.set(setting);
	}

	public boolean userHasRoles(Long userId, List<RolesEnum> roles) {
		return userRepository.userHasRoles(userId, roles);
	}

}
