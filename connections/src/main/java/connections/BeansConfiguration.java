package connections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import email.MailService;

@Configuration(value = "connections.BeansConfiguration")
public class BeansConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean(name = "connections.mailService")
	public MailService mailService() {
		MailService mailService = new MailService();
		return mailService;
	}
}
