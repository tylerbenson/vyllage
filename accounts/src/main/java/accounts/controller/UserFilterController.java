package accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.model.UserFilterRequest;
import accounts.model.UserFilterResponse;
import accounts.service.UserService;

@Controller
@RequestMapping("user")
public class UserFilterController {

	@Autowired
	private UserService userService;

	public @ResponseBody UserFilterResponse findUsers(
			@RequestBody UserFilterRequest filter) {

		UserFilter userFilter = new UserFilter();

		return userFilter.filter(
				filter,
				userService.getUser(SecurityContextHolder.getContext()
						.getAuthentication().getName()));
	}

}
