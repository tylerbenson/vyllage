package jobs.mocks.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration(value = "jobs.mocks.config.MockBeansConfiguration")
@Import(value = jobs.configuration.BeansConfiguration.class)
public class MockBeansConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		return Mockito.mock(RestTemplate.class);
	}

}
