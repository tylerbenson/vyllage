package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import accounts.repository.FirstLoginException;
import accounts.repository.UserNotFoundException;
import accounts.service.SignInUtil;

@Controller
public class LoginController {

	private final Logger logger = Logger.getLogger(LoginController.class
			.getName());

	private final SignInUtil signInUtil;

	@Inject
	public LoginController(final SignInUtil signInUtil) {
		this.signInUtil = signInUtil;
	}

	// http://localhost:8080/login
	@RequestMapping("/login")
	public String login(HttpServletRequest request,
			@RequestParam(value = "error", required = false) String error,
			Model model) throws UserNotFoundException {

		if (error != null) {

			Exception exception = (Exception) request.getSession()
					.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

			Throwable cause = exception.getCause();
			if (exception != null
					&& exception instanceof InternalAuthenticationServiceException
					&& cause != null && cause instanceof FirstLoginException) {

				logger.info(cause.getMessage());

				signInUtil.signIn(((FirstLoginException) cause).getUserId());

				return "redirect:account/reset-password-forced";
			}

		}

		return "login";
	}

	// http://localhost:8080/expire
	@RequestMapping("/expire")
	public String expire() {
		return "expire";
	}

}
