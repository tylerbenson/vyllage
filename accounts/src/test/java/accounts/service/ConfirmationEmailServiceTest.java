package accounts.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;

import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import user.common.User;
import accounts.model.Email;
import accounts.model.account.ConfirmEmailLink;
import accounts.repository.EmailRepository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;

public class ConfirmationEmailServiceTest {

	private ConfirmationEmailService confirmationEmailService;

	private Environment environment = mock(Environment.class);

	private EmailBuilder emailBuilder = mock(EmailBuilder.class,
			new SelfReturningAnswer());

	private EmailRepository emailRepository = mock(EmailRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	private TextEncryptor encryptor = new MockTextEncryptor();

	@Before
	public void setUp() throws Exception {

		confirmationEmailService = new ConfirmationEmailService(environment,
				emailBuilder, mapper, encryptor, emailRepository);
	}

	@Test
	public void testSendConfirmationEmail() throws EmailException {

		Long userId = 0L;
		String userName = "email@email.com";
		String firstName = "firstName";
		String anotherEmail = "another@email.com";

		User user = mock(User.class);

		when(user.getUserId()).thenReturn(userId);
		when(user.getUsername()).thenReturn(userName);
		when(user.getFirstName()).thenReturn(firstName);

		Email email = new Email();
		email.setConfirmed(false);
		email.setDefaultEmail(false);
		email.setEmail(anotherEmail);
		email.setUserId(user.getUserId());

		confirmationEmailService.sendConfirmationEmail(user, email);
		Mockito.verify(emailRepository).save(email);
		Mockito.verify(emailBuilder).send();
	}

	@Test
	public void testGetEncodedLink() throws JsonParseException,
			JsonMappingException, IOException {

		Long userId = 0L;
		String email = "anEmail@email.com";

		ConfirmEmailLink confirmEmailLink = new ConfirmEmailLink(userId, email);
		String encodedLink = confirmationEmailService
				.getEncodedLink(confirmEmailLink);

		String decodedString = new String(Base64.getUrlDecoder().decode(
				encodedLink));

		String changeEmail = encryptor.decrypt(decodedString);

		ConfirmEmailLink confirmationLink = mapper.readValue(changeEmail,
				ConfirmEmailLink.class);

		assertEquals(userId, confirmationLink.getUserId());
		assertEquals(email, confirmationLink.getEmail());

	}

	@Test
	public void testSendEmail() throws EmailException {
		String email = "anEmail@email.com";
		String encodedConfirmEmailLink = "string";
		String firstName = "firstName";

		confirmationEmailService.sendEmail(email, encodedConfirmEmailLink,
				firstName);

		Mockito.verify(emailBuilder).send();
	}

	/**
	 * From
	 * https://stackoverflow.com/questions/8501920/how-to-mock-a-builder-with
	 * -mockito
	 */
	class SelfReturningAnswer implements Answer<Object> {

		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			Object mock = invocation.getMock();
			if (invocation.getMethod().getReturnType().isInstance(mock)) {
				return mock;
			} else {
				return Mockito.RETURNS_DEFAULTS.answer(invocation);
			}
		}
	}

	/**
	 * Mock encryptor for Solano, since it doesn't support JCE8...
	 * 
	 * @author uh
	 */
	class MockTextEncryptor implements TextEncryptor {

		@Override
		public String encrypt(String text) {
			return text;
		}

		@Override
		public String decrypt(String encryptedText) {
			return encryptedText;
		}

	}

}
