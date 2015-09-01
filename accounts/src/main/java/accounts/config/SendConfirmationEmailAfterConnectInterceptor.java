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
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import accounts.model.account.ConfirmEmailLink;

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

	public SendConfirmationEmailAfterConnectInterceptor(
			Environment environment, final EmailBuilder emailBuilder,
			ObjectMapper mapper, TextEncryptor encryptor) {
		this.environment = environment;
		this.emailBuilder = emailBuilder;
		this.mapper = mapper;
		this.encryptor = encryptor;
	}

	@Override
	public void preConnect(ConnectionFactory<Facebook> connectionFactory,
			MultiValueMap<String, String> parameters, WebRequest request) {

	}

	@Override
	public void postConnect(Connection<Facebook> connection, WebRequest request) {
		if (SecurityContextHolder.getContext().getAuthentication() == null
				|| SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal() == null)
			return;

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		String email = connection.fetchUserProfile().getEmail();

		// emails differ, must confirm
		if (!user.getUsername().equalsIgnoreCase(email)) {

			try {
				sendConfirmationEmail(email,
						getEncodedString(user.getUserId()), user.getFirstName());
			} catch (JsonProcessingException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		}

	}

	protected String getEncodedString(Long userId)
			throws JsonProcessingException {
		ConfirmEmailLink link = new ConfirmEmailLink(userId);

		String jsonConfirmEmailLink = mapper.writeValueAsString(link);

		String encryptedString = encryptor.encrypt(jsonConfirmEmailLink);

		String encodedString = Base64.getUrlEncoder().encodeToString(
				encryptedString.getBytes());

		return encodedString;
	}

	/**
	 * Sends email confirmation.
	 * 
	 * @param email
	 * @param encodedString
	 * @param name
	 * @throws EmailException
	 */
	protected void sendConfirmationEmail(String email, String encodedString,
			String name) {

		final String txt = "https://"
				+ environment.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/account/email-confirmation/";

		Runnable run = () -> {
			try {
				emailBuilder
						.from(environment.getProperty("email.from",
								"no-reply@vyllage.com"))
						.fromUserName(
								environment.getProperty("email.from.userName",
										"Chief of Vyllage"))
						.subject("Email Confirmation").to(email)
						.templateName("email-confirm").setNoHtmlMessage(txt)
						.addTemplateVariable("userName", name)
						.addTemplateVariable("url", txt)
						.addTemplateVariable("encodedLink", encodedString)
						.send();
			} catch (Exception e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		run.run();

	}

}
