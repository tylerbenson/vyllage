package accounts.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutorService;

import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import user.common.User;
import accounts.mocks.MockTextEncryptor;
import accounts.mocks.SelfReturningAnswer;
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

	private ExecutorService executorService = mock(ExecutorService.class);

	@Before
	public void setUp() throws Exception {

		confirmationEmailService = new ConfirmationEmailService(environment,
				emailBuilder, mapper, encryptor, emailRepository,
				executorService);

		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Runnable run = invocation.getArgumentAt(0, Runnable.class);
				run.run();
				return null;
			}
		}).when(executorService).execute(Mockito.any(Runnable.class));
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

		boolean defaultEmail = false;
		boolean confirmed = false;
		Email email = new Email(user.getUserId(), anotherEmail, defaultEmail,
				confirmed);

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

}
