package accounts.config;

import java.util.logging.Logger;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import accounts.model.Email;
import accounts.service.ConfirmationEmailService;

public class SendConfirmationEmailAfterConnectInterceptor implements
		ConnectInterceptor<Facebook> {

	private final Logger logger = Logger
			.getLogger(SendConfirmationEmailAfterConnectInterceptor.class
					.getName());

	private final ConfirmationEmailService confirmationEmailService;

	public SendConfirmationEmailAfterConnectInterceptor(
			final ConfirmationEmailService confirmationEmailService) {
		this.confirmationEmailService = confirmationEmailService;
	}

	@Override
	public void preConnect(ConnectionFactory<Facebook> connectionFactory,
			MultiValueMap<String, String> parameters, WebRequest request) {
		// nothing to do.
	}

	@Override
	public void postConnect(Connection<Facebook> connection, WebRequest request) {
		if (SecurityContextHolder.getContext().getAuthentication() == null
				|| getUser() == null)
			return;

		User user = (User) getUser();

		final String emailString = connection.fetchUserProfile().getEmail();

		// emails differ, must confirm
		if (!user.getUsername().equalsIgnoreCase(emailString)) {

			Email email = new Email(user.getUserId(), emailString, false, false);

			logger.info("Sending confirmation email: " + email
					+ " for Facebook connection.");

			confirmationEmailService.sendConfirmationEmail(user, email);

		}

	}

	protected Object getUser() {
		return SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}

}
