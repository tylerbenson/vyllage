package accounts.mocks.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import accounts.config.BeansConfiguration;
import accounts.mocks.MockTextEncryptor;
import accounts.mocks.SelfReturningAnswer;
import email.EmailBuilder;

/**
 * This is to mock EmailBuilder to avoid sending mails and TextEncryptor because
 * Solano doesn't support JCE8.
 * 
 * @author uh
 */
@Configuration(value = "accounts.mocks.config.MockBeansConfiguration")
@Import(value = BeansConfiguration.class)
public class MockBeansConfiguration {

	// these beans 'override' the ones in BeansConfiguration, Spring
	// just loads the last defined bean.
	@Bean
	public TextEncryptor textEncryptor() {
		return new MockTextEncryptor();
	}

	@Bean(name = "accounts.emailBuilder")
	public EmailBuilder emailBuilder(SpringTemplateEngine templateEngine,
			Environment environment) {
		return Mockito.mock(EmailBuilder.class, new SelfReturningAnswer());
	}

	@Bean
	public RestTemplate restTemplate() {
		return Mockito.mock(RestTemplate.class);
	}
}
