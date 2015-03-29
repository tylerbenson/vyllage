package accounts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.email.EmailContext;
import accounts.email.EmailHTMLBody;
import accounts.email.EmailParameters;
import accounts.email.MailService;
import accounts.model.User;
import accounts.model.account.AccountNames;
import accounts.model.account.ChangePasswordForm;
import accounts.model.account.ResetPasswordForm;
import accounts.repository.UserNotFoundException;
import accounts.service.DocumentLinkService;
import accounts.service.ResetPasswordLink;
import accounts.service.UserService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("account")
public class AccountController {

	private static final int limitForEmptyFilter = 5;

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

	/**
	 * Returns a list containing the id, first, middle and last names for an
	 * specific user.
	 * 
	 * @param userId
	 * @return
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "names", method = RequestMethod.GET, produces = "application/json")
	// @PreAuthorize("hasAuthority('USER')")
	public @ResponseBody List<AccountNames> getNames(
			@RequestParam("userIds") final List<Long> userIds)
			throws UserNotFoundException {

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
			@RequestParam(value = "excludeIds", required = false) final List<Long> excludeIds)
			throws UserNotFoundException {
		User user = userService.getUser(userId);

		List<AccountNames> response = userService
				.getAdvisors(user, limitForEmptyFilter)
				.stream()
				.filter(u -> !excludeIds.contains(u.getUserId()))
				.map(u -> new AccountNames(u.getUserId(), u.getFirstName(), u
						.getMiddleName(), u.getLastName()))
				.collect(Collectors.toList());

		return response;
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

		sendEmail(user.getUsername(), encodedString, user.getFirstName());

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

	protected void sendEmail(String email, String encodedString, String userName)
			throws EmailException {
		EmailParameters parameters = new EmailParameters("Reset Password",
				email);

		String txt = "http://"
				+ env.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/account/reset-password-change/";

		System.out.println(txt);

		EmailContext ctx = new EmailContext(env.getProperty(
				"change.password.html", "changePassword-email"));
		ctx.setVariable("userName", userName);
		ctx.setVariable("url", txt);
		ctx.setVariable("encodedLink", encodedString);

		EmailHTMLBody emailBody = new EmailHTMLBody(txt, ctx);
		mailService.sendEmail(parameters, emailBody);
	}

	private boolean isEmailValid(ResetPasswordForm resetPassword) {
		boolean isValid = resetPassword.isValid()
				&& userService.userExists(resetPassword.getEmail());
		resetPassword.setError(!isValid);
		return isValid;
	}
}