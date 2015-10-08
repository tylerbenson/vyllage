package accounts.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/accounts/application-dev.properties")
@Configuration(value = "accounts.config.PropertyConfigProd")
@Profile("dev")
@ConditionalOnMissingClass(name = "accounts.config.PropertyConfigProd")
public class PropertyConfigDev {

}
