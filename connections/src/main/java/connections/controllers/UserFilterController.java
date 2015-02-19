package connections.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import connections.model.UserFilterRequest;
import connections.model.UserFilterResponse;

@Controller
@RequestMapping("user")
public class UserFilterController {

	private UserFilter userFilter = new UserFilter();

	public @ResponseBody UserFilterResponse findUsers(
			@RequestBody UserFilterRequest filter) {

		return userFilter.filter(filter);
	}

}
