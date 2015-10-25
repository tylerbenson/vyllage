package accounts.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.togglz.console.TogglzConsoleServlet;

import accounts.config.beans.ApplicationContextProvider;
import accounts.config.beans.SimpleSignInAdapter;
import accounts.repository.EmailRepository;
import accounts.repository.SharedDocumentRepository;
import accounts.service.ConfirmationEmailService;
import accounts.service.utilities.BatchParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import email.EmailBuilder;
import email.MailService;

@Configuration(value = "accounts.BeansConfiguration")
public class BeansConfiguration {

	@Autowired
	private DataSource dataSource;

	@Value("${social.password:hjQb7K3Nsgv5DL6kDNeRAR}")
	private final String SOCIAL_PASSWORD = null;

	@Value("${social.salt:686a5162374b334e73677635444c366b444e65524152}")
	private final String SOCIAL_SALT = null;

	@Bean(name = "accounts.objectMapper")
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean(name = "accounts.emailBuilder")
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		EmailBuilder emailBuilder = new EmailBuilder(new MailService(
				templateEngine), environment);

		return emailBuilder;
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
	public SignInAdapter signInAdapter(UserDetailsService userDetailsService,
			SharedDocumentRepository sharedDocumentRepository) {
		return new SimpleSignInAdapter(userDetailsService,
				new HttpSessionRequestCache(), sharedDocumentRepository);
	}

	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.queryableText(SOCIAL_PASSWORD, SOCIAL_SALT);
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
			@Qualifier("accounts.objectMapper") ObjectMapper mapper,
			TextEncryptor encryptor,
			EmailRepository emailRepository,
			@Qualifier(value = "accounts.ExecutorService") ExecutorService executorService) {
		return new ConfirmationEmailService(environment, emailBuilder, mapper,
				encryptor, emailRepository, executorService);
	}

	@Bean
	public BatchParser batchParser() {
		return new BatchParser();
	}

	@Bean
	public ProviderSignInUtils providerSignInUtils(
			ConnectionFactoryLocator connectionFactoryLocator,
			UsersConnectionRepository connectionRepository) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				connectionRepository);
	}
}
