package site;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/connections/application-prod.properties")
@PropertySource("classpath:/documents/application-prod.properties")
@PropertySource("classpath:/accounts/application-prod.properties")
@Configuration
@Profile(Profiles.PROD)
public class PropertyConfigProd {

}
