package editor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeansConfiguration {
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// TODO: for the future
		// restTemplate.setInterceptors(Collections
		// .singletonList(new TokenAuthenticationInterceptor()));
		return restTemplate;
	}

}
