package connections;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/connections/application-prod.properties")
@Configuration(value = "connections.PropertyConfigProd")
@Profile("prod")
public class PropertyConfigProd {

}
