package jobs.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@Configuration(value = "jobs.BeansConfiguration")
public class BeansConfiguration {
	@Autowired
	private Environment environment;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public Java8TimeDialect Java8TimeDialect() {
		return new Java8TimeDialect();
	}
}
