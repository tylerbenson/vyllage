package documents.mocks.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import documents.configuration.BeansConfiguration;
import documents.mocks.SelfReturningAnswer;
import email.EmailBuilder;

@Configuration(value = "documents.mocks.config.MockBeansConfiguration")
@Import(value = BeansConfiguration.class)
public class MockBeansConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		return Mockito.mock(RestTemplate.class);
	}

	@Bean(name = "documents.emailBuilder")
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		return Mockito.mock(EmailBuilder.class, new SelfReturningAnswer());
	}

}
