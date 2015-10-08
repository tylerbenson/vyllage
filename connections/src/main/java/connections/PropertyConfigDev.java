package connections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/connections/application-dev.properties")
@Configuration(value = "connections.PropertyConfigDev")
@Profile("dev")
@ConditionalOnMissingClass(name = "connections.PropertyConfigProd")
public class PropertyConfigDev {

}
