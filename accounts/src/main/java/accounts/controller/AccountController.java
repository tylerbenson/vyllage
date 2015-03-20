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

	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public String postResetPassword() {
		// validate email and return error if not in the database.
		// If submit success:
		// generate a new password reset link (similar to document share link)
		// email it to the provided email address.
		//
		// Link should log the user in and direct them to the password change
		// page.
		return "reset-password";
	}
}