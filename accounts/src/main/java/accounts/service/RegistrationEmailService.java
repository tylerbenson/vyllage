package accounts.service;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

@Service
public class RegistrationEmailService {

	private final Logger logger = Logger
			.getLogger(RegistrationEmailService.class.getName());

	private final EmailBuilder emailBuilder;
	private final Environment environment;
	private final ExecutorService executorService;

	@Inject
	public RegistrationEmailService(
			final Environment environment,
			@Qualifier("accounts.emailBuilder") final EmailBuilder emailBuilder,
			@Qualifier(value = "accounts.ExecutorService") final ExecutorService executorService) {
		this.emailBuilder = emailBuilder;
		this.environment = environment;
		this.executorService = executorService;
	}

	/**
	 * Sends an email with the password to the user.
	 * 
	 * @param email
	 * @param password
	 * @param firstName
	 */
	public void sendUserRegisteredEmail(@NonNull final String email,
			@NonNull final String password, @NonNull final String firstName) {

		this.registrationEmail(email, password, firstName,
				"email-user-registered");

	}

	public void sendAutomatedAccountCreationEmail(@NonNull final String email,
			@NonNull final String password, @NonNull final String firstName) {

		this.registrationEmail(email, password, firstName,
				"email-account-created");

	}

	protected void registrationEmail(final String email, String password,
			final String firstName, final String template) {

		Runnable run = () -> {

			try {

				String from = environment.getProperty("email.from",
						"no-reply@vyllage.com");

				String fromUserName = environment.getProperty(
						"email.from.userName", "Chief of Vyllage");

				String noHTMLMessage = "Your account has been created successfuly. \\n Your password is: "
						+ password;

				emailBuilder.to(email).from(from).fromUserName(fromUserName)
						.subject("Account Creation - Vyllage.com")
						.setNoHtmlMessage(noHTMLMessage).templateName(template)
						.addTemplateVariable("password", password)
						.addTemplateVariable("firstName", firstName).send();
			} catch (EmailException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		executorService.execute(run);
	}
}
