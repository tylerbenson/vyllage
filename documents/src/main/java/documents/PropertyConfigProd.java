package documents;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/documents/application-prod.properties")
@Configuration(value = "documents.PropertyConfigProd")
@Profile("prod")
public class PropertyConfigProd {

}
