package login.controller;

import login.model.link.LinkRequest;
import login.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("link")
public class DocumentLinkController {
	@Autowired
	UserService userService;

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(@RequestBody LinkRequest linkRequest) {

		User user = userService.getUser(linkRequest.getName());
		if (user == null)
			userService.createUser(linkRequest.getName());

		return "";
	}
}
