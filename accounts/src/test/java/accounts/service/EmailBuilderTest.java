package accounts.service;

import org.apache.commons.mail.EmailException;

import accounts.email.EmailBuilder;

/**
 * Mock class for the email builder.
 */
public class EmailBuilderTest extends EmailBuilder {

	@Override
	public void send() throws EmailException {
		// do nothing
	}

}
