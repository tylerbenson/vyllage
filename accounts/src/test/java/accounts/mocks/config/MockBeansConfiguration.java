package accounts.mocks.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.client.RestTemplate;
import org.togglz.console.TogglzConsoleServlet;

import user.common.social.SimpleSignInAdapter;
import accounts.config.beans.ApplicationContextProvider;
import accounts.mocks.MockTextEncryptor;
import accounts.mocks.SelfReturningAnswer;
import accounts.repository.EmailRepository;
import accounts.service.ConfirmationEmailService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import email.EmailBuilder;

/**
 * This is to mock EmailBuilder to avoid sending mails and TextEncryptor because
 * Solano doesn't support JCE8.
 * 
 * @author uh
 */
@Configuration(value = "accounts.mocks.config.MockBeansConfiguration")
public class MockBeansConfiguration {

	@Autowired
	private DataSource dataSource;

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean(name = "accounts.emailBuilder")
	public EmailBuilder emailBuilder() {
		return Mockito.mock(EmailBuilder.class, new SelfReturningAnswer());
	}

	// this should probably go in site but since we are handling security from
	// account too, I'm keeping it here. Same for the TogglzRequestMatcher.
	@Bean
	public ServletRegistrationBean togglzConsole() {
		return new ServletRegistrationBean(new TogglzConsoleServlet(),
				"/togglz/*");
	}

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}

	@Bean
	public ApplicationContextProvider applicationContextProvider(
			ApplicationContext applicationContext) {
		ApplicationContextProvider provider = new ApplicationContextProvider(
				applicationContext);
		return provider;
	}

	@Bean
	public SignInAdapter signInAdapter(UserDetailsService userDetailsService) {
		return new SimpleSignInAdapter(userDetailsService,
				new HttpSessionRequestCache());
	}

	@Bean
	public TextEncryptor textEncryptor() {
		return new MockTextEncryptor();
	}

	@Bean(name = "accounts.ExecutorService")
	public ExecutorService executorService() {
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		return newCachedThreadPool;
	}

	@Bean
	public ConfirmationEmailService confirmationEmailService(
			Environment environment,
			@Qualifier("accounts.emailBuilder") EmailBuilder emailBuilder,
			ObjectMapper mapper,
			TextEncryptor encryptor,
			EmailRepository emailRepository,
			@Qualifier(value = "accounts.ExecutorService") ExecutorService executorService) {
		return new ConfirmationEmailService(environment, emailBuilder, mapper,
				encryptor, emailRepository, executorService);
	}
}
