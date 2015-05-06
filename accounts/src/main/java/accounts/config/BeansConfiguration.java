package accounts.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.togglz.console.TogglzConsoleServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import email.EmailBuilder;
import email.MailService;

@Configuration(value = "accounts.BeansConfiguration")
public class BeansConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SpringTemplateEngine templateEngine;

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
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		EmailBuilder emailBuilder = new EmailBuilder(new MailService(
				templateEngine), environment);

		return emailBuilder;
	}

	// this should probably go in site but since we are handling security from
	// account too, I'm keeping it here. Same for the TogglzRequestMatcher.
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		return new ServletRegistrationBean(new TogglzConsoleServlet(),
				"/togglz/*");
	}

	// @Bean
	// public TogglzConfiguration togglzConfiguration(DataSource dataSource) {
	// return new TogglzConfiguration(dataSource);
	// }

	// @Bean
	// public SingletonFeatureManagerProvider singletonFeatureManagerProvider(
	// TogglzConfiguration togglzConfiguration) {
	// SingletonFeatureManagerProvider provider = new
	// SingletonFeatureManagerProvider(
	// togglzConfiguration);
	//
	// return provider;
	// }

	// @Bean
	// public FeatureManager featureManager(DataSource dataSource) {
	// FeatureManager featureManager = FeatureManagerBuilder.begin()
	// .name("features").featureEnum(Features.class)
	// .togglzConfig(new TogglzConfiguration(dataSource)).build();
	// return featureManager;
	// }

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}

	@Bean
	public ApplicationContextProvider applicationContextProvider(
			ApplicationContext applicationContext) {
		ApplicationContextProvider provider = new ApplicationContextProvider();
		provider.setApplicationContext(applicationContext);
		return provider;
	}

}
