package accounts.config.handlers;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NonNull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;

import accounts.controller.LoginController;
import accounts.model.Email;
import accounts.repository.EmailRepository;
import accounts.service.ConfirmationEmailService;

public class ConfirmEmailAddressSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler implements
		ApplicationContextAware {

	private final Logger logger = Logger.getLogger(LoginController.class
			.getName());

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(
			@NonNull ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		super.onAuthenticationSuccess(request, response, authentication);

		EmailRepository emailRepository = this.applicationContext
				.getBean(EmailRepository.class);
		ConfirmationEmailService confirmationEmailService = this.applicationContext
				.getBean(ConfirmationEmailService.class);

		Assert.notNull(authentication);
		Assert.notNull(authentication.getPrincipal());

		Optional<Email> email = emailRepository.getByEmail(authentication
				.getName());

		// if the user could login then he has received the email
		if (email.isPresent() && !email.get().isConfirmed()) {
			logger.info("User " + authentication.getPrincipal()
					+ " logged in with uncorfimed email. Confirming...");

			confirmationEmailService.confirmEmail(email.get());

		}

	}

}
