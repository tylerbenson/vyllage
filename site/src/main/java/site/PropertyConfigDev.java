package site;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/connections/application-dev.properties")
@PropertySource("classpath:/documents/application-dev.properties")
@PropertySource("classpath:/accounts/application-dev.properties")
@Configuration(value = "site.PropertyConfigDev")
@Profile(Profiles.DEV)
@ConditionalOnMissingClass(name = "site.PropertyConfigProd")
public class PropertyConfigDev {

}
