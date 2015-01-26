package profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("classpath:/profile/application.properties")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
