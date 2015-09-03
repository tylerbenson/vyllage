package accounts.service;

import java.util.Base64;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.User;
import accounts.model.Email;
import accounts.model.account.ConfirmEmailLink;
import accounts.repository.EmailRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

@Service
public class ConfirmationEmailService {

	private final Logger logger = Logger
			.getLogger(ConfirmationEmailService.class.getName());

	private final Environment environment;

	private final EmailBuilder emailBuilder;

	private final ObjectMapper mapper;

	private final TextEncryptor encryptor;

	private final EmailRepository emailRepository;

	@Inject
	public ConfirmationEmailService(Environment environment,
			@Qualifier("accounts.emailBuilder") EmailBuilder emailBuilder,
			ObjectMapper mapper, TextEncryptor encryptor,
			EmailRepository emailRepository) {
		this.environment = environment;
		this.emailBuilder = emailBuilder;
		this.mapper = mapper;
		this.encryptor = encryptor;
		this.emailRepository = emailRepository;
	}

	public void sendConfirmationEmail(User user, Email email) {

		String encodedConfirmEmailLink = this
				.getEncodedLink(new ConfirmEmailLink(user.getUserId(), email
						.getEmail()));

		this.sendEmail(email.getEmail(), encodedConfirmEmailLink,
				user.getFirstName());

		// TODO: if the email can't be sent for whatever reason the user will be
		// stuck with an unverified email address, we need to add a link in
		// account settings showing the email as unverified and add some link to
		// re-send the email.

		this.emailRepository.save(email);
	}

	protected String getEncodedLink(ConfirmEmailLink link) {

		// ConfirmEmailLink link = new ConfirmEmailLink(userId, email);

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
	protected void sendEmail(final String email,
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
