package accounts.config;

import java.util.Base64;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import accounts.model.Email;
import accounts.model.account.ConfirmEmailLink;
import accounts.repository.EmailRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

public class SendConfirmationEmailAfterConnectInterceptor implements
		ConnectInterceptor<Facebook> {

	private final Logger logger = Logger
			.getLogger(SendConfirmationEmailAfterConnectInterceptor.class
					.getName());

	private final Environment environment;

	private final EmailBuilder emailBuilder;

	private final ObjectMapper mapper;

	private final TextEncryptor encryptor;

	private final EmailRepository emailRepository;

	public SendConfirmationEmailAfterConnectInterceptor(
			final Environment environment, final EmailBuilder emailBuilder,
			final EmailRepository emailRepository, final ObjectMapper mapper,
			final TextEncryptor encryptor) {
		this.environment = environment;
		this.emailBuilder = emailBuilder;
		this.emailRepository = emailRepository;
		this.mapper = mapper;
		this.encryptor = encryptor;
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

			Email email = new Email();
			email.setConfirmed(false);
			email.setDefaultEmail(false);
			email.setEmail(emailString);
			email.setUserId(user.getUserId());
			emailRepository.save(email);

			String encodedConfirmEmailLink = this.getEncodedLink(
					user.getUserId(), emailString);

			this.sendConfirmationEmail(emailString, encodedConfirmEmailLink,
					user.getFirstName());

		}

	}

	protected Object getUser() {
		return SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}

	protected String getEncodedLink(Long userId, String email) {

		ConfirmEmailLink link = new ConfirmEmailLink(userId, email);

		String jsonConfirmEmailLink = null;

		try {

			jsonConfirmEmailLink = mapper.writeValueAsString(link);

		} catch (JsonProcessingException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		Assert.notNull(jsonConfirmEmailLink);

		String encryptedString = encryptor.encrypt(jsonConfirmEmailLink);

		String encodedString = Base64.getUrlEncoder().encodeToString(
				encryptedString.getBytes());

		return encodedString;
	}

	/**
	 * Sends email confirmation.
	 * 
	 * @param email
	 * @param encodedConfirmEmailLink
	 * @param name
	 * @throws EmailException
	 */
	protected void sendConfirmationEmail(final String email,
			final String encodedConfirmEmailLink, final String firstName) {

		String domain = environment.getProperty("vyllage.domain",
				"www.vyllage.com");

		final String txt = "https://" + domain
				+ "/account/email/email-confirmation/";

		Runnable run = () -> {
			try {
				String from = environment.getProperty("email.from",
						"no-reply@vyllage.com");

				final String fromUserName = environment.getProperty(
						"email.from.userName", "Chief of Vyllage");

				emailBuilder
						.from(from)
						.fromUserName(fromUserName)
						.subject("Email Confirmation")
						.to(email)
						.templateName("email-confirm")
						.setNoHtmlMessage(
								txt + "?encodedLink=" + encodedConfirmEmailLink)
						.addTemplateVariable("firstName", firstName)
						.addTemplateVariable("url", txt)
						.addTemplateVariable("encodedLink",
								encodedConfirmEmailLink).send();
			} catch (Exception e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		run.run();

	}

}
