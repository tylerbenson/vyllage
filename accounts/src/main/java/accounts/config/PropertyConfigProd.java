package accounts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/accounts/application-prod.properties")
@Configuration(value = "accounts.config.PropertyConfigProd")
@Profile("prod")
public class PropertyConfigProd {

}
