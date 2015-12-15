package accounts.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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

	/**
	 * http://stackoverflow.com/questions/34125425/spring-social-facebook-login-
	 * error-numeric-value-out-of-range-of-int <br>
	 * This is fixed in Spring Social Facebook 2.0.3 but Jackson insists the
	 * field 'size' is an int in VideoUploadLimits even though is long. Changing
	 * Jackson versions does not fix it. <br>
	 * Since we don't need videos right now I'll remove the field.
	 */
	@PostConstruct
	private void init() {
		// hack for the facebook login
		try {
			String[] fieldsToMap = { "id", "about", "age_range", "address",
					"bio", "birthday", "context", "cover", "currency",
					"devices", "education", "email", "favorite_athletes",
					"favorite_teams", "first_name", "gender", "hometown",
					"inspirational_people", "installed", "install_type",
					"is_verified", "languages", "last_name", "link", "locale",
					"location", "meeting_for", "middle_name", "name",
					"name_format", "political", "quotes",
					"payment_pricepoints", "relationship_status", "religion",
					"security_settings", "significant_other", "sports",
					"test_group", "timezone", "third_party_id", "updated_time",
					"verified", "viewer_can_send_gift", "website", "work" };

			Field field = Class.forName(
					"org.springframework.social.facebook.api.UserOperations")
					.getDeclaredField("PROFILE_FIELDS");
			field.setAccessible(true);

			Field modifiers = field.getClass().getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, fieldsToMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
