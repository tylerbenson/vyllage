package login.controller;

import java.util.HashMap;
import java.util.Map;

import login.model.User;
import login.repository.UserNotFoundException;
import login.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("account")
public class AccountController {

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
}
