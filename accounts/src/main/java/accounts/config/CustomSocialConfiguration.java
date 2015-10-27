package accounts.config;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.web.DisconnectController;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;

import accounts.config.beans.SendConfirmationEmailAfterConnectInterceptor;
import accounts.controller.ConnectControllerWithRedirect;
import accounts.service.ConfirmationEmailService;

@Configuration
@EnableSocial
public class CustomSocialConfiguration extends SocialConfigurerAdapter {

	private final Logger logger = Logger
			.getLogger(CustomSocialConfiguration.class.getName());
	// https://github.com/spring-projects/spring-social-samples/blob/master/spring-social-showcase-sec/src/main/java/org/springframework/social/showcase/config/SocialConfig.java
	// also SocialAutoConfigurationAdapter

	@Value(value = "${social.base.url}")
	private String SOCIAL_BASE_URL;

	@Inject
	private DataSource dataSource;

	@Inject
	private TextEncryptor textEncryptor;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(
			ConnectionFactoryLocator connectionFactoryLocator) {

		JdbcUsersConnectionRepository jdbcUsersConnectionRepository = new JdbcUsersConnectionRepository(
				dataSource, connectionFactoryLocator, textEncryptor);
		jdbcUsersConnectionRepository.setTablePrefix("ACCOUNTS.");
		return jdbcUsersConnectionRepository;
	}

	@Bean
	public ConnectController connectController(
			ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository,
			ConfirmationEmailService confirmationEmailService) {

		ConnectControllerWithRedirect connectController = new ConnectControllerWithRedirect(
				connectionFactoryLocator, connectionRepository);

		connectController
				.addInterceptor(new SendConfirmationEmailAfterConnectInterceptor(
						confirmationEmailService));

		connectController.setApplicationUrl(SOCIAL_BASE_URL);

		// connectController.addInterceptor(new
		// PostToWallAfterConnectInterceptor());
		// connectController.addInterceptor(new TweetAfterConnectInterceptor());
		return connectController;
	}

	@Bean
	public ProviderSignInController providerSignInController(
			ConnectionFactoryLocator connectionFactoryLocator,
			UsersConnectionRepository usersConnectionRepository,
			SignInAdapter signInAdapter) {
		ProviderSignInController providerSignInController = new ProviderSignInController(
				connectionFactoryLocator, usersConnectionRepository,
				signInAdapter);
		providerSignInController.setApplicationUrl(SOCIAL_BASE_URL);
		return providerSignInController;
	}

	@Override
	@Bean
	public UserIdSource getUserIdSource() {
		return new UserIdSource() {
			@Override
			public String getUserId() {
				Authentication authentication = SecurityContextHolder
						.getContext().getAuthentication();
				if (authentication == null) {
					throw new IllegalStateException(
							"Unable to get a ConnectionRepository: no user signed in");
				}
				return authentication.getName();
			}
		};
	}

	@Bean
	public DisconnectController disconnectController(
			UsersConnectionRepository usersConnectionRepository,
			Environment environment) {
		String facebookAppSecret = environment
				.getRequiredProperty("spring.social.facebook.appSecret");

		return new DisconnectController(usersConnectionRepository,
				facebookAppSecret);
	}

	@Bean
	public ReconnectFilter apiExceptionHandler(
			UsersConnectionRepository usersConnectionRepository,
			UserIdSource userIdSource) {
		return new ReconnectFilter(usersConnectionRepository, userIdSource);
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Google google(ConnectionRepository repository) {
		Connection<Google> connection = repository
				.findPrimaryConnection(Google.class);
		return connection != null ? connection.getApi() : null;
	}

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig,
			Environment environment) {
		super.addConnectionFactories(cfConfig, environment);

		String googleAppId = environment
				.getRequiredProperty("spring.social.google.appId");

		String facebookAppId = environment
				.getRequiredProperty("spring.social.facebook.appId");

		logger.info("Google appId" + googleAppId);

		logger.info("Facebook appId" + facebookAppId);

		String googleAppSecret = environment
				.getRequiredProperty("spring.social.google.appSecret");

		cfConfig.addConnectionFactory(new GoogleConnectionFactory(googleAppId,
				googleAppSecret));
	}
}
