package documents.mocks.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;

import togglz.controller.TogglzFeatureController;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import documents.mocks.SelfReturningAnswer;
import email.EmailBuilder;

@Configuration(value = "documents.mocks.config.MockBeansConfiguration")
public class MockBeansConfiguration {
	@Autowired
	private Environment environment;

	@Bean
	public RestTemplate restTemplate() {
		return Mockito.mock(RestTemplate.class);
	}

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}

	@Bean
	public Java8TimeDialect Java8TimeDialect() {
		return new Java8TimeDialect();
	}

	@Bean(name = "documents.emailBuilder")
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		return Mockito.mock(EmailBuilder.class, new SelfReturningAnswer());
	}

	@Bean
	public TogglzFeatureController togglzController() {
		return new TogglzFeatureController();
	}

}
