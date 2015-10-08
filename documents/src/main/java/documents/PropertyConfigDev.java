package documents;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/documents/application-dev.properties")
@Configuration(value = "documents.PropertyConfigDev")
@Profile("dev")
@ConditionalOnMissingClass(name = "documents.PropertyConfigProd")
public class PropertyConfigDev {

}
