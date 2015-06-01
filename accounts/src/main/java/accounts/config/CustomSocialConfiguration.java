package accounts.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.facebook.web.DisconnectController;

@Configuration
@EnableSocial
public class CustomSocialConfiguration extends SocialConfigurerAdapter {
	// https://github.com/spring-projects/spring-social-samples/blob/master/spring-social-showcase-sec/src/main/java/org/springframework/social/showcase/config/SocialConfig.java
	// also SocialAutoConfigurationAdapter

	@Inject
	private DataSource dataSource;

	@Inject
	private Environment enviroment;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(
			ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository jdbcUsersConnectionRepository = new JdbcUsersConnectionRepository(
				dataSource, connectionFactoryLocator, Encryptors.queryableText(
						enviroment.getRequiredProperty("social.password"),
						enviroment.getRequiredProperty("social.salt")));
		jdbcUsersConnectionRepository.setTablePrefix("ACCOUNTS.");
		return jdbcUsersConnectionRepository;
	}

	@Bean
	public ConnectController connectController(
			ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {

		ConnectController connectController = new ConnectController(
				connectionFactoryLocator, connectionRepository);
		connectController.setViewPath("/");
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

}
