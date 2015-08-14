package accounts.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	private final Logger logger = Logger.getLogger(LoginController.class
			.getName());

	// http://localhost:8080/login
	@RequestMapping("/login")
	public String login(HttpServletRequest request,
			@RequestParam(value = "error", required = false) String error,
			Model model) {

		if (error != null) {

			Exception exception = (Exception) request.getSession()
					.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

			if (exception instanceof CredentialsExpiredException) {

				error = exception.getMessage();

				CredentialsExpiredException credentialsExpiredException = (CredentialsExpiredException) exception;
				if (credentialsExpiredException.getAuthentication() != null)
					logger.info(credentialsExpiredException.getAuthentication()
							.toString());

				return null;
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
