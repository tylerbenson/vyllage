package accounts.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import accounts.model.form.RegisterForm;
import accounts.service.UserService;

@Controller
public class RegisterController {

	@Inject
	private UserService userService;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(Model model) {

		RegisterForm registerForm = new RegisterForm();

		model.addAttribute("registerForm", registerForm);
		return "register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@RequestBody RegisterForm registerForm, Model model) {

		if (registerForm.isValid()) {
			userService.createUser(registerForm);
			return "registration-completed";
		}

		model.addAttribute("registerForm", registerForm);

		return "register";
	}
}
