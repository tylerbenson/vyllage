package accounts.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/connections/application-dev.properties")
@PropertySource("classpath:/documents/application-dev.properties")
@PropertySource("classpath:/accounts/application-dev.properties")
@Configuration
@Profile("dev")
@ConditionalOnMissingClass(name = "accounts.PropertyConfigProd")
public class PropertyConfigDev {

}
