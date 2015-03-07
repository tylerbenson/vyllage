package accounts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	// http://localhost:8080/login
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	// http://localhost:8080/expire
	@RequestMapping("/expire")
	public String expire() {
		return "expire";
	}

}
