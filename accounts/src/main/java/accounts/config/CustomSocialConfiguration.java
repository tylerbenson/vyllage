package accounts.config;

import javax.inject.Inject;
import javax.sql.DataSource;

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
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.facebook.web.DisconnectController;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.util.Assert;

import accounts.config.beans.SendConfirmationEmailAfterConnectInterceptor;
import accounts.controller.ConnectControllerWithRedirect;
import accounts.service.ConfirmationEmailService;

@Configuration
@EnableSocial
public class CustomSocialConfiguration extends SocialConfigurerAdapter {
	// https://github.com/spring-projects/spring-social-samples/blob/master/spring-social-showcase-sec/src/main/java/org/springframework/social/showcase/config/SocialConfig.java
	// also SocialAutoConfigurationAdapter

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
		connectController.setApplicationUrl("https://www.vyllage.com");

		// connectController.addInterceptor(new
		// PostToWallAfterConnectInterceptor());
		// connectController.addInterceptor(new TweetAfterConnectInterceptor());
		return connectController;
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
		return new DisconnectController(usersConnectionRepository,
				environment.getProperty("facebook.appSecret"));
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
			Environment env) {
		super.addConnectionFactories(cfConfig, env);

		Assert.notNull(env.getProperty("spring.social.google.appId"));
		Assert.notNull(env.getProperty("spring.social.google.appSecret"));

		cfConfig.addConnectionFactory(new GoogleConnectionFactory(env
				.getProperty("spring.social.google.appId"), env
				.getProperty("spring.social.google.appSecret")));
	}
}
