package accounts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
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
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.ChangeEmailLink;
import accounts.model.account.ChangePasswordForm;
import accounts.model.account.ResetPasswordForm;
import accounts.model.account.ResetPasswordLink;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentLinkService;
import accounts.service.UserService;
import accounts.service.contactSuggestion.UserContactSuggestionService;

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

	private final AccountSettingsService accountSettingsService;

	private final UserContactSuggestionService userContactSuggestionService;

	private final TextEncryptor encryptor;

	private final ObjectMapper mapper;

	@Autowired
	@Qualifier(value = "accounts.emailBuilder")
	private EmailBuilder emailBuilder;

	@Inject
	public AccountController(final Environment environment,
			final UserService userService,
			final DocumentLinkService documentLinkService,
			final AccountSettingsService accountSettingsService,
			final UserContactSuggestionService userContactSuggestionService,
			final TextEncryptor encryptor, final ObjectMapper mapper) {
		super();
		this.environment = environment;
		this.userService = userService;
		this.documentLinkService = documentLinkService;
		this.accountSettingsService = accountSettingsService;
		this.userContactSuggestionService = userContactSuggestionService;
		this.encryptor = encryptor;
		this.mapper = mapper;
	}

	@ModelAttribute("userInfo")
	public AccountContact userInfo(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		List<AccountContact> contactDataForUsers = userService
				.getAccountContactForUsers(accountSettingsService
						.getAccountSettings(Arrays.asList(user.getUserId())));

		if (contactDataForUsers.isEmpty()) {
			return null;
		}
		return contactDataForUsers.get(0);
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
	public @ResponseBody List<AccountNames> getAdvisorsForUser(
			@PathVariable final Long userId,
			@RequestParam(value = "excludeIds", required = false) final List<Long> excludeIds,
			@RequestParam(value = "firstNameFilter", required = false) String firstNameFilter,
			@RequestParam(value = "lastNameFilter", required = false) String lastNameFilter,
			@RequestParam(value = "emailFilter", required = false) String emailFilter)
			throws UserNotFoundException {
		User user = userService.getUser(userId);

		Map<String, String> filters = new HashMap<>();

		if (firstNameFilter != null)
			filters.put("firstName", firstNameFilter);

		if (lastNameFilter != null)
			filters.put("lastName", lastNameFilter);

		if (emailFilter != null)
			filters.put("email", emailFilter);

		if (excludeIds != null && !excludeIds.isEmpty())
			return userContactSuggestionService
					.getSuggestions(user, filters, limitForEmptyFilter)
					.stream()
					.filter(u -> !excludeIds.contains(u.getUserId()))
					.map(u -> new AccountNames(u.getUserId(), u.getFirstName(),
							u.getMiddleName(), u.getLastName()))
					.collect(Collectors.toList());
		else
			return userContactSuggestionService
					.getSuggestions(user, filters, limitForEmptyFilter)
					.stream()
					.map(u -> new AccountNames(u.getUserId(), u.getFirstName(),
							u.getMiddleName(), u.getLastName()))
					.collect(Collectors.toList());
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.DELETE,
			RequestMethod.POST }, consumes = "application/x-www-form-urlencoded;charset=UTF-8", produces = "text/html")
	public String deleteUser(HttpServletRequest request,
			@AuthenticationPrincipal User user) throws ServletException,
			UserNotFoundException {

		userService.delete(request, user.getUserId());

		return "user-deleted";
	}

	@RequestMapping(value = "reset-password", method = RequestMethod.GET)
	public String getResetPassword(Model model) {
		model.addAttribute("resetPasswordForm", new ResetPasswordForm());
		return "reset-password";
	}

	@RequestMapping(value = "reset-password", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public String postResetPassword(ResetPasswordForm resetPassword)
			throws JsonProcessingException, UnsupportedEncodingException,
			EmailException {

		// validate email and return error if not in the database.
		if (!isEmailValid(resetPassword))
			return "reset-password";

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
		return "reset-password-success";
	}

	@RequestMapping(value = "/reset-password-change", method = RequestMethod.GET)
	public String changePassword(
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

		return "reset-password-change";
	}

	@RequestMapping(value = "/reset-password-change", method = RequestMethod.POST)
	@PreAuthorize("isAuthenticated()")
	public String postChangePassword(ChangePasswordForm form, Model model) {

		if (!form.isValid()) {
			form.setError(true);
			model.addAttribute("changePasswordForm", form);
			return "reset-password-change";
		}

		userService.changePassword(form.getNewPassword());

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		Authentication auth = new UsernamePasswordAuthenticationToken(
				authentication.getName(), form.getNewPassword(),
				authentication.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

		return "password-change-success";
	}

	protected void sendResetPasswordEmail(String email, String encodedString,
			String userName) throws EmailException {

		String txt = "https://"
				+ environment.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/account/reset-password-change/";

		emailBuilder
				.from(environment.getProperty("email.from",
						"no-reply@vyllage.com"))
				.fromUserName(
						environment.getProperty("email.from.userName",
								"Chief of Vyllage")).subject("Reset Password")
				.to(email).templateName("email-change-password")
				.setNoHtmlMessage(txt)
				.addTemplateVariable("userName", userName)
				.addTemplateVariable("url", txt)
				.addTemplateVariable("encodedLink", encodedString).send();
	}

	private boolean isEmailValid(ResetPasswordForm resetPassword) {
		boolean isValid = resetPassword.isValid()
				&& userService.userExists(resetPassword.getEmail());
		resetPassword.setError(!isValid);
		return isValid;
	}

	@RequestMapping(value = "contact", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountContact> getContactInformation(
			@RequestParam(value = "userIds", required = true) final List<Long> userIds) {

		return userService.getAccountContactForUsers(accountSettingsService
				.getAccountSettings(userIds));
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

		return userService.getAvatar(userId);
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
			throw new AccessDeniedException("Invalid link provided.");

		userService.changeEmail(user, changeEmailLink.getNewEmail());

		return "email-change-success";
	}

}
