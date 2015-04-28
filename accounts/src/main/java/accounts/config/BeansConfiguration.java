package accounts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;
import email.MailService;

@Configuration(value = "accounts.BeansConfiguration")
public class BeansConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean(name = "accounts.mailService")
	public MailService mailService() {
		MailService mailService = new MailService();
		return mailService;
	}

	@Bean(name = "accounts.emailBuilder")
	@Autowired
	public EmailBuilder emailBuilder(Environment environment) {
		EmailBuilder emailBuilder = new EmailBuilder(mailService(), environment);

		return emailBuilder;
	}
}
