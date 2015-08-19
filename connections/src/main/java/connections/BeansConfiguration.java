package connections;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;
import email.MailService;

@Configuration(value = "connections.BeansConfiguration")
public class BeansConfiguration {

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

	@Bean(name = "connections.ExecutorService")
	public ExecutorService executorService() {
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		return newCachedThreadPool;
	}
}
