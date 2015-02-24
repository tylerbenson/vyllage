package login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class LoginController {

	@Autowired
	private ObjectMapper mapper;

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
