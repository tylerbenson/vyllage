package accounts.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import accounts.mocks.MockTextEncryptor;
import accounts.mocks.SelfReturningAnswer;
import accounts.model.Email;
import accounts.repository.EmailRepository;
import accounts.service.ConfirmationEmailService;

import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;

public class SendConfirmationEmailAfterConnectInterceptorTest {

	private SendConfirmationEmailAfterConnectInterceptor interceptor;

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

		interceptor = new SendConfirmationEmailAfterConnectInterceptor(
				confirmationEmailService);

		when(environment.getProperty("vyllage.domain", "www.vyllage.com"))
				.thenReturn("www.vyllage.com");
		when(environment.getProperty("email.from", "no-reply@vyllage.com"))
				.thenReturn("no-reply@vyllage.com");
		when(environment.getProperty("email.from.userName", "Chief of Vyllage"))
				.thenReturn("Chief of Vyllage");

	}

	@Test
	public void testPostConnect() throws EmailException {

		Long userId = 0L;
		String userName = "email@email.com";
		String firstName = "firstName";
		String anotherEmail = "another@email.com";

		User user = mock(User.class);

		when(user.getUserId()).thenReturn(userId);
		when(user.getUsername()).thenReturn(userName);
		when(user.getFirstName()).thenReturn(firstName);

		@SuppressWarnings("unchecked")
		Connection<Facebook> connection = mock(Connection.class);
		WebRequest request = mock(WebRequest.class);

		UserProfile userProfile = mock(UserProfile.class);
		when(connection.fetchUserProfile()).thenReturn(userProfile);

		when(userProfile.getEmail()).thenReturn(anotherEmail);

		Authentication authentication = mock(Authentication.class);

		when(authentication.getPrincipal()).thenReturn(user);

		SecurityContext securityContext = mock(SecurityContext.class);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		interceptor.postConnect(connection, request);

		Mockito.verify(emailRepository).save(Mockito.any(Email.class));
		Mockito.verify(emailBuilder).send();

	}

}
