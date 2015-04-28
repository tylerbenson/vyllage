package accounts.service;

import org.apache.commons.mail.EmailException;
import org.springframework.core.env.Environment;

import email.EmailBuilder;
import email.MailService;

/**
 * Mock class for the email builder.
 */
public class EmailBuilderTest extends EmailBuilder {

	public EmailBuilderTest(MailService mailService, Environment environment) {
		super(mailService, environment);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void send() throws EmailException {
		// do nothing
	}

}
