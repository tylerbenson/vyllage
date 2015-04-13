package accounts.service;

import org.apache.commons.mail.EmailException;

import accounts.email.EmailBuilder;

public class EmailBuilderTest extends EmailBuilder {

	@Override
	public void send() throws EmailException {
		// do nothing
	}

}
