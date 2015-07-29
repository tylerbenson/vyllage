package accounts.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.User;
import accounts.model.form.RegisterForm;
import accounts.service.SignInUtil;
import accounts.service.UserService;

@Controller
public class RegisterController {

	private final SignInUtil signInUtil;

	@Inject
	private final UserService userService;

	@Inject
	public RegisterController(SignInUtil signInUtil, UserService userService) {
		this.signInUtil = signInUtil;
		this.userService = userService;

	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(Model model) {

		RegisterForm registerForm = new RegisterForm();

		model.addAttribute("registerForm", registerForm);
		return "register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public String register(HttpServletRequest request,
			RegisterForm registerForm, Model model) {

		if (userService.userExists(registerForm.getEmail())) {
			registerForm.setErrorMsg("User already registered.");
			model.addAttribute("registerForm", registerForm);

			return "register";
		}

		if (registerForm.isValid()) {

			User user = userService.createUser(registerForm);
			signInUtil.signIn(request, user, registerForm.getPassword());

			return "registration-completed";
		}

		model.addAttribute("registerForm", registerForm);

		return "register";
	}
}
