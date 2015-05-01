package documents;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;

@Configuration(value = "documents.BeansConfiguration")
public class BeansConfiguration {
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public TogglzDialect togglzDialect() {
		return new TogglzDialect();
	}

}
