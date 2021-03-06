package accounts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.AccountSettingsEnum;
import user.common.web.AccountContact;
import user.common.web.UserInfo;
import util.web.constants.AccountUrlConstants;
import accounts.model.Email;
import accounts.model.account.AccountNames;
import accounts.model.account.ChangeEmailLink;
import accounts.model.account.ChangePasswordForm;
import accounts.model.account.ResetPasswordForm;
import accounts.model.account.ResetPasswordLink;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.EmailRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.ConfirmationEmailService;
import accounts.service.DocumentLinkService;
import accounts.service.UserService;
import accounts.service.contactSuggestion.UserContactSuggestionService;
import accounts.validation.PhoneNumberValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;

@Controller
@RequestMapping("account")
public class AccountController {

	private static final int limitForEmptyFilter = 5;

	private final Logger logger = Logger.getLogger(AccountController.class
			.getName());

	private final Environment environment;

	private final UserService userService;

	private final DocumentLinkService documentLinkService;

	private final UserContactSuggestionService userContactSuggestionService;

	private final AccountSettingsService accountSettingsService;

	private final EmailRepository emailRepository;

	private final EmailBuilder emailBuilder;

	private final TextEncryptor encryptor;

	private final ObjectMapper mapper;

	private final ConfirmationEmailService confirmationEmailService;

	@Inject
	public AccountController(
			final Environment environment,
			final UserService userService,
			final DocumentLinkService documentLinkService,
			final UserContactSuggestionService userContactSuggestionService,
			final AccountSettingsService accountSettingsService,
			final ConfirmationEmailService confirmationEmailService,
			final EmailRepository emailRepository,
			@Qualifier(value = "accounts.emailBuilder") final EmailBuilder emailBuilder,
			final TextEncryptor encryptor,
			@Qualifier("accounts.objectMapper") final ObjectMapper mapper) {
		super();
		this.environment = environment;
		this.userService = userService;
		this.documentLinkService = documentLinkService;
		this.userContactSuggestionService = userContactSuggestionService;
		this.accountSettingsService = accountSettingsService;
		this.confirmationEmailService = confirmationEmailService;
		this.emailRepository = emailRepository;
		this.emailBuilder = emailBuilder;
		this.encryptor = encryptor;
		this.mapper = mapper;
	}

	@ModelAttribute("userInfo")
	public UserInfo userInfo(@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		UserInfo userInfo = new UserInfo(user);
		userInfo.setEmailConfirmed(confirmationEmailService
				.isEmailConfirmed(user.getUserId()));

		return userInfo;
	}

	@RequestMapping(value = "roles", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<String> getUserRoles(
			@AuthenticationPrincipal User user) {

		return user.getAuthorities().stream().map(a -> a.getAuthority())
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list containing the id, first, middle and last names for an
	 * specific user.
	 *
	 * @param userId
	 * @return
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "names", method = RequestMethod.GET, produces = "application/json")
	// @PreAuthorize("hasAuthority('...')")
	public @ResponseBody List<AccountNames> getNames(
			@RequestParam(value = "userIds", required = true) final List<Long> userIds) {

		return userService.getNames(userIds);
	}

	/**
	 * Returns a list of advisors names for an specific user.
	 *
	 * @param userId
	 * @return
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "{userId}/advisors", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountContact> getAdvisorsForUser(
			HttpServletRequest request,
			@PathVariable final Long userId,
			@RequestParam(value = "excludeIds", required = false) final List<Long> excludeIds,
			@RequestParam(value = "firstNameFilter", required = false) String firstNameFilter,
			@RequestParam(value = "lastNameFilter", required = false) String lastNameFilter,
			@RequestParam(value = "emailFilter", required = false) String emailFilter)
			throws UserNotFoundException {
		User user = userService.getUser(userId);

		final List<Long> excludedIdsCopy = new ArrayList<>();
		// adding self
		excludedIdsCopy.add(user.getUserId());

		if (excludeIds != null && !excludeIds.isEmpty())
			excludedIdsCopy.addAll(excludeIds);

		Map<String, String> filters = new HashMap<>();

		if (firstNameFilter != null)
			filters.put("firstName", firstNameFilter);

		if (lastNameFilter != null)
			filters.put("lastName", lastNameFilter);

		if (emailFilter != null)
			filters.put("email", emailFilter);

		List<User> users = userContactSuggestionService
				.getSuggestions(user, filters, limitForEmptyFilter).stream()
				.filter(u -> !excludedIdsCopy.contains(u.getUserId()))
				.collect(Collectors.toList());

		return accountSettingsService.getAccountContacts(request, users
				.stream().map(u -> u.getUserId()).collect(Collectors.toList()));
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.DELETE,
			RequestMethod.POST }, consumes = "application/x-www-form-urlencoded;charset=UTF-8", produces = "text/html")
	public String deleteUser(HttpServletRequest request,
			@AuthenticationPrincipal User user) throws ServletException,
			UserNotFoundException {

		userService.delete(request, user.getUserId());

		return AccountUrlConstants.USER_DELETED;
	}

	@RequestMapping(value = "reset-password", method = RequestMethod.GET)
	public String getResetPassword(Model model) {
		model.addAttribute("resetPasswordForm", new ResetPasswordForm());
		return AccountUrlConstants.RESET_PASSWORD;
	}

	@RequestMapping(value = "reset-password", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public String postResetPassword(ResetPasswordForm resetPassword)
			throws JsonProcessingException, UnsupportedEncodingException,
			EmailException {

		// validate email and return error if not in the database.
		if (!isEmailValid(resetPassword))
			return AccountUrlConstants.RESET_PASSWORD;

		// If submit success:
		// generate a new password reset link (similar to document share link)
		User user = userService.getUser(resetPassword.getEmail());

		ResetPasswordLink link = documentLinkService
				.createResetPasswordLink(user);

		System.out.println("link " + link);

		String jsonLink = mapper.writeValueAsString(link);

		System.out.println(" json link" + jsonLink);

		String encodedString = Base64.getUrlEncoder().encodeToString(
				jsonLink.getBytes());

		System.out.println("encoded " + encodedString);

		// email it to the provided email address.

		sendResetPasswordEmail(user.getUsername(), encodedString,
				user.getFirstName());

		// Link should log the user in and direct them to the password change
		// page.
		return AccountUrlConstants.RESET_PASSWORD_SUCCESS;
	}

	@RequestMapping(value = "reset-password-change", method = RequestMethod.GET)
	public String resetPassword(
			@RequestParam(value = "resetPassword", required = true) String resetPassword,
			Model model) throws JsonParseException, JsonMappingException,
			IOException, UserNotFoundException {

		String encodedString = new String(Base64.getUrlDecoder().decode(
				resetPassword));

		ResetPasswordLink resetLink = mapper.readValue(encodedString,
				ResetPasswordLink.class);

		User user = userService.getUser(resetLink.getUserId());

		Authentication auth = new UsernamePasswordAuthenticationToken(
				user.getUsername(), user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

		model.addAttribute("changePasswordForm", new ChangePasswordForm());

		return AccountUrlConstants.RESET_PASSWORD_CHANGE;
	}

	@RequestMapping(value = "reset-password-change", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public String postResetPassword(HttpServletRequest request,
			ChangePasswordForm form, Model model) throws ServletException {

		if (!form.isValid()) {
			form.setError(true);
			model.addAttribute("changePasswordForm", form);
			return AccountUrlConstants.RESET_PASSWORD_CHANGE;
		}

		userService.changePassword(form.getNewPassword());

		request.logout();

		return AccountUrlConstants.PASSWORD_CHANGE_SUCCESS;
	}

	@RequestMapping(value = "reset-password-forced", method = RequestMethod.GET)
	@PreAuthorize("isAuthenticated()")
	public String resetPasswordForced(Model model) {
		logger.info("Reset password was forced.");
		model.addAttribute("changePasswordForm", new ChangePasswordForm());

		return AccountUrlConstants.RESET_PASSWORD_FORCED;
	}

	@RequestMapping(value = "reset-password-forced", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public String postResetPasswordForced(HttpServletRequest request,
			@AuthenticationPrincipal User user, ChangePasswordForm form,
			Model model) throws ServletException {

		logger.info("Reset password was forced.");

		if (!form.isValid()) {
			form.setError(true);
			model.addAttribute("changePasswordForm", form);
			return AccountUrlConstants.RESET_PASSWORD_FORCED;
		}

		userService.forcedPasswordChange(user.getUserId(), user.getUsername(),
				form.getNewPassword());

		request.logout();

		return AccountUrlConstants.PASSWORD_CHANGE_SUCCESS;
	}

	protected void sendResetPasswordEmail(String email, String encodedString,
			String userName) throws EmailException {

		String txt = "https://"
				+ environment.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/account/reset-password-change/";

		emailBuilder
				.from(environment.getProperty("email.from",
						"chief@vyllage.com"))
				.fromUserName(
						environment.getProperty("email.from.userName",
								"Chief of Vyllage")).subject("Reset Password")
				.to(email).templateName("email-change-password")
				.setNoHtmlMessage(txt)
				.addTemplateVariable("userName", userName)
				.addTemplateVariable("url", txt)
				.addTemplateVariable("encodedLink", encodedString).send();
	}

	protected boolean isEmailValid(ResetPasswordForm resetPassword) {
		boolean isValid = resetPassword.isValid()
				&& userService.userExists(resetPassword.getEmail());
		resetPassword.setError(!isValid);
		return isValid;
	}

	@RequestMapping(value = "contact", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountContact> getContactInformation(
			HttpServletRequest request,
			@RequestParam(value = "userIds", required = true) final List<Long> userIds) {

		return accountSettingsService.getAccountContacts(request, userIds);
	}

	@RequestMapping(value = "ping", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void ping(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		HttpSession session = request.getSession();

		logger.info("User " + user.getFirstName() + " is alive. "
				+ "Last Access: " + new Date(session.getLastAccessedTime()));
	}

	@RequestMapping(value = "{userId}/organization/{organizationId}/roles", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody List<String> adminRoleManagement(
			@PathVariable Long userId, @PathVariable Long organizationId)
			throws UserNotFoundException {
		User user = userService.getUser(userId);

		if (user.getAuthorities() == null || user.getAuthorities().isEmpty())
			return Collections.emptyList();

		return user
				.getAuthorities()
				.stream()
				.filter(uor -> ((UserOrganizationRole) uor).getOrganizationId()
						.equals(organizationId))
				.map(a -> a.getAuthority().toUpperCase())
				.collect(Collectors.toList());
	}

	/**
	 * Returns an url pointing to the user's avatar.
	 *
	 * @param userId
	 * @return
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "{userId}/avatar", method = RequestMethod.GET)
	public @ResponseBody String getAvatar(@PathVariable Long userId)
			throws UserNotFoundException {

		return accountSettingsService.getAvatar(userId);
	}

	@RequestMapping(value = "change-email", method = RequestMethod.GET)
	public String confirmChangeEmailAddress(
			@RequestParam(value = "changeEmail", required = true) String encodedString,
			@AuthenticationPrincipal User user) throws JsonParseException,
			JsonMappingException, IOException, UserNotFoundException {

		String decodedString = new String(Base64.getUrlDecoder().decode(
				encodedString));

		String changeEmail = encryptor.decrypt(decodedString);

		ChangeEmailLink changeEmailLink = mapper.readValue(changeEmail,
				ChangeEmailLink.class);

		if (!user.getUserId().equals(changeEmailLink.getUserId()))
			throw new AccessDeniedException("User " + user
					+ " provided an invalid link: " + changeEmail);

		userService.changeEmail(user, changeEmailLink.getNewEmail());

		return AccountUrlConstants.EMAIL_CHANGE_SUCCESS;
	}

	@RequestMapping(value = "{userId}/can-request-feedback", method = RequestMethod.GET)
	public @ResponseBody Boolean canRequestFeedback(
			@PathVariable(value = "userId") Long userId,
			@AuthenticationPrincipal User user) {

		List<Email> byUserId = emailRepository.getByUserId(userId);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.phoneNumber.name());

		boolean noEmails = byUserId == null || byUserId.isEmpty();

		PhoneNumberValidator validator = new PhoneNumberValidator();

		boolean noPhoneNumber = !accountSetting.isPresent();

		noPhoneNumber |= accountSetting.isPresent()
				&& StringUtils.isBlank(accountSetting.get().getValue());

		noPhoneNumber |= accountSetting.isPresent()
				&& validator.validate(accountSetting.get()).getErrorMessage() != null;

		if (noEmails || noPhoneNumber)
			return false;

		return byUserId.stream().allMatch(e -> e.isConfirmed())
				&& !noPhoneNumber;

	}

	@RequestMapping(value = "{userId}/email-confirmed", method = RequestMethod.GET)
	public @ResponseBody Boolean emailConfirmed(
			@PathVariable(value = "userId") Long userId) {

		return confirmationEmailService.isEmailConfirmed(userId);

	}

}
