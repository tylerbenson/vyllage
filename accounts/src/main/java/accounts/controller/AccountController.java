package accounts.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.model.FilteredUser;
import accounts.model.User;
import accounts.repository.UserNotFoundException;
import accounts.service.UserService;

@Controller
@RequestMapping("account")
public class AccountController {

	private static final int limitForEmptyFilter = 5;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "names/{userId}", method = RequestMethod.GET, produces = "application/json")
	// @PreAuthorize("hasAuthority('USER')")
	public @ResponseBody Map<String, String> getNamesForUser(
			@PathVariable final Long userId) throws UserNotFoundException {
		User user = userService.getUser(userId);

		Map<String, String> names = new HashMap<>();
		names.put("firstName", user.getFirstName());
		names.put("middleName", user.getMiddleName());
		names.put("lastName", user.getLastName());

		return names;
	}

	@RequestMapping(value = "advisors/{userId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<FilteredUser> getAdvisorsForUser(
			@PathVariable final Long userId) throws UserNotFoundException {
		User user = userService.getUser(userId);

		List<FilteredUser> response = userService
				.getAdvisors(user, limitForEmptyFilter)
				.stream()
				.map(u -> new FilteredUser(u.getUserId(), u.getFirstName()
						+ " " + u.getLastName())).collect(Collectors.toList());

		System.out.println("Advisors: ");
		response.forEach(System.out::println);

		return response;
	}

}