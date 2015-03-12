package accounts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	@RequestMapping(value = "names", method = RequestMethod.POST, produces = "application/json")
	// @PreAuthorize("hasAuthority('USER')")
	public @ResponseBody List<AccountNames> getNames(
			@RequestBody final List<Long> userIds) throws UserNotFoundException {

		return userService.getNames(userIds);
	}

	/**
	 * Returns a list of advisors names for an specific user.
	 * 
	 * @param userId
	 * @return
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "advisors/{userId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountNames> getAdvisorsForUser(
			@PathVariable final Long userId) throws UserNotFoundException {
		User user = userService.getUser(userId);

		List<AccountNames> response = userService
				.getAdvisors(user, limitForEmptyFilter)
				.stream()
				.map(u -> new AccountNames(u.getUserId(), u.getFirstName(), u
						.getMiddleName(), u.getLastName()))
				.collect(Collectors.toList());

		return response;
	}

}