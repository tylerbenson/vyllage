package documents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile({ "dev", "prod" })
@SpringBootApplication
@PropertySource("classpath:/documents/application-${profile}.properties")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}
}
