package documents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import togglz.controller.TogglzFeatureController;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import email.EmailBuilder;
import email.MailService;

@Configuration(value = "documents.BeansConfiguration")
public class BeansConfiguration {
	@Autowired
	private Environment environment;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}

	@Bean(name = "documents.emailBuilder")
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		EmailBuilder emailBuilder = new EmailBuilder(new MailService(
				templateEngine), environment);

		return emailBuilder;
	}

	@Bean
	public TogglzFeatureController togglzController() {
		return new TogglzFeatureController();
	}

}
