package accounts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.model.User;
import accounts.model.account.AccountNames;
import accounts.repository.UserNotFoundException;
import accounts.service.UserService;

@Controller
@RequestMapping("account")
public class AccountController {

	private static final int limitForEmptyFilter = 5;

	@Autowired
	private UserService userService;

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

	@RequestMapping(value = "/reset-password", method = RequestMethod.GET)
	public String getResetPassword() {
		return "reset-password";
	}

	@RequestMapping(value = "/reset-password-change/{resetPassword}", method = RequestMethod.GET)
	public String changePassword(@PathVariable String resetPassword)
			throws JsonParseException, JsonMappingException, IOException {

		String encodedString = new String(Base64.getUrlDecoder().decode(
				resetPassword.getBytes("UTF-8")));

		ResetPasswordLink resetLink = mapper.readValue(encodedString,
				ResetPasswordLink.class);

		return "reset-password-change";
	}

	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public String postResetPassword(@RequestBody ResetPasswordForm resetPassword)
			throws JsonProcessingException, UnsupportedEncodingException,
			EmailException {
		// validate email and return error if not in the database.
		if (!isValid(resetPassword))
			return "reset-password";

		// If submit success:
		// generate a new password reset link (similar to document share link)
		User user = userService.getUserByEmail(resetPassword.getEmail());

		ResetPasswordLink link = documentLinkService
				.createResetPasswordLink(user);

		String jsonLink = mapper.writeValueAsString(link);

		String encodedString = Base64.getUrlEncoder().encodeToString(
				jsonLink.getBytes("UTF-8"));

		// email it to the provided email address.

		sendEmail(user.getUsername(), encodedString);

		// Link should log the user in and direct them to the password change
		// page.
		return "reset-password-success";
	}

	protected void sendEmail(String email, String encodedString)
			throws EmailException {
		EmailParameters parameters = new EmailParameters(null,
				"Reset Password", email);

		String txt = env.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/password-change?resetPassword=" + encodedString;

		EmailContext ctx = new EmailContext(env.getProperty(
				"change.password.html", "changePassword-email"));
		ctx.setVariable("passwordLink", txt);

		EmailHTMLBody emailBody = new EmailHTMLBody(txt, ctx);
		mailService.sendEmail(parameters, emailBody);
	}

	private boolean isValid(ResetPasswordForm resetPassword) {
		return resetPassword.isValid(); // && validate in DB
	}
}