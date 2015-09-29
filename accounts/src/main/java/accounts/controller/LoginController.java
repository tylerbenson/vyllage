package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jooq.tools.StringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import accounts.repository.PasswordResetWasForcedException;
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
			@RequestParam(value = "error", required = false) String error)
			throws UserNotFoundException {

		if (error != null) {

			Exception exception = (Exception) request.getSession()
					.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

			if (exception != null
					&& exception instanceof InternalAuthenticationServiceException
					&& exception.getCause() != null
					&& exception.getCause() instanceof PasswordResetWasForcedException) {

				PasswordResetWasForcedException ex = (PasswordResetWasForcedException) exception
						.getCause();

				logger.info(ex.getMessage());

				signInUtil.signIn(ex.getUser());

				return "redirect:/account/reset-password-forced";
			}

		}

		return "login";
	}

	// http://localhost:8080/expire
	@RequestMapping("/expire")
	public String expire(
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "message", required = false) String message,
			Model model) {

		if (StringUtils.isBlank(message)) {
			model.addAttribute("title", "Your session has expired.");
			model.addAttribute(
					"message",
					"Please go back to the <a href=\"/login\"\\>login page</a> and enter your credentials.");
		} else {
			model.addAttribute("title", title);
			model.addAttribute("message", message);
		}
		return "expire";
	}

}
