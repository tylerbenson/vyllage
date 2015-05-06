package connections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;

import email.MailService;

@Configuration(value = "connections.BeansConfiguration")
public class BeansConfiguration {

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean(name = "connections.MailService")
	public MailService mailService(SpringTemplateEngine templateEngine) {
		MailService mailService = new MailService(templateEngine);
		return mailService;
	}

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}
}