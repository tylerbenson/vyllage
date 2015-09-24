package accounts.service;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;

import user.common.User;
import accounts.model.Email;
import accounts.model.account.ConfirmEmailLink;
import accounts.repository.ElementNotFoundException;
import accounts.repository.EmailRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

/**
 * Service used to save new emails and request confirmation.
 * 
 * @author uh
 */
public class ConfirmationEmailService {

	private final Logger logger = Logger
			.getLogger(ConfirmationEmailService.class.getName());

	private final Environment environment;

	private final EmailBuilder emailBuilder;

	private final ObjectMapper mapper;

	private final TextEncryptor encryptor;

	private final EmailRepository emailRepository;

	private ExecutorService executorService;

	@Inject
	public ConfirmationEmailService(
			@NonNull final Environment environment,
			@NonNull final EmailBuilder emailBuilder,
			@Qualifier("accounts.objectMapper") @NonNull final ObjectMapper mapper,
			@NonNull final TextEncryptor encryptor,
			@NonNull final EmailRepository emailRepository,
			@NonNull @Qualifier(value = "accounts.ExecutorService") ExecutorService executorService) {
		this.environment = environment;
		this.emailBuilder = emailBuilder;
		this.mapper = mapper;
		this.encryptor = encryptor;
		this.emailRepository = emailRepository;
		this.executorService = executorService;
	}

	public void sendConfirmationEmail(@NonNull User user, @NonNull Email email) {
		Assert.notNull(user.getUserId());
		Assert.notNull(email.getUserId());
		Assert.notNull(email.getEmail());
		Assert.isTrue(user.getUserId().equals(email.getUserId()));

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

		final String domain = environment.getProperty("vyllage.domain",
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
			} catch (EmailException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		executorService.execute(run);

	}

	/**
	 * Sets an email as confirmed.
	 * 
	 * @param email
	 */
	public void confirmEmail(Email email) {
		email.setConfirmed(true);
		emailRepository.save(email);

		logger.info("Email " + email + " confirmed.");
	}

	/**
	 * Confirms email change for user name.
	 * 
	 * @param oldEmail
	 * @param newEmail
	 */
	public void confirmEmailChange(String oldEmail, String newEmail) {
		Optional<Email> optional = emailRepository.getByEmail(oldEmail);

		if (optional.isPresent()) {
			Email email = optional.get();
			email.setConfirmed(true);
			email.setEmail(newEmail);
			this.confirmEmail(email);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Tried to confirm email change from '") //
					.append(oldEmail) //
					.append("' to '").append(newEmail) //
					.append("' but the old email was not found."); //

			ElementNotFoundException e = new ElementNotFoundException(
					sb.toString());

			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}
	}

}
