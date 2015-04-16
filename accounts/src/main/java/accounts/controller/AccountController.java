package accounts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.email.EmailBuilder;
import accounts.email.MailService;
import accounts.model.CSRFToken;
import accounts.model.User;
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.account.ChangePasswordForm;
import accounts.model.account.ResetPasswordForm;
import accounts.model.account.ResetPasswordLink;
import accounts.repository.UserNotFoundException;
import accounts.service.DocumentLinkService;
import accounts.service.TokenHelper;
import accounts.service.UserService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("account")
public class AccountController {

	private static final int limitForEmptyFilter = 5;

	private final Logger logger = Logger.getLogger(AccountController.class
			.getName());

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;

	@Autowired
	private DocumentLinkService documentLinkService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MailService mailService;

	@Autowired
	private EmailBuilder emailBuilder;

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

		List<AccountNames> response = userService
				.getAdvisors(user, filters, limitForEmptyFilter)
				.stream()
				.filter(u -> !excludeIds.contains(u.getUserId()))
				.map(u -> new AccountNames(u.getUserId(), u.getFirstName(), u
						.getMiddleName(), u.getLastName()))
				.collect(Collectors.toList());

		return response;
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.DELETE,
			RequestMethod.POST }, consumes = "application/x-www-form-urlencoded;charset=UTF-8", produces = "text/html")
	public String deleteUser(HttpServletRequest request)
			throws ServletException, UserNotFoundException {

		CSRFToken token = new CSRFToken();
		token.setValue(TokenHelper.getToken(request).getToken());

		userService.delete(request, getUser().getUserId(), token);

		return "user-deleted";
	}

	@RequestMapping(value = "{userId}/delete", method = RequestMethod.DELETE, produces = "application/json")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminDeleteUser(HttpServletRequest request,
			@PathVariable Long userId, @RequestBody CSRFToken token)
			throws ServletException, UserNotFoundException {

		userService.delete(request, userId, token);
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

		String txt = "http://"
				+ env.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/account/reset-password-change/";

		emailBuilder
				.from(env.getProperty("email.userName", "no-reply@vyllage.com"))
				.subject("Reset Password")
				.to(email)
				.templateName(
						env.getProperty("change.password.html",
								"email-changePassword")).setNoHtmlMessage(txt)
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

		return userService.getAccountContactForUsers(userIds);
	}

	private User getUser() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		return (User) auth.getPrincipal();
	}
}
