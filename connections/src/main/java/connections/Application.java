package connections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import util.profiles.Profiles;

@Profile({ Profiles.DEV, Profiles.PROD })
@SpringBootApplication(scanBasePackages = { "connections.controller",
		"connections.repository", "connections.service" })
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class,
		RepositoryRestMvcAutoConfiguration.class })
@PropertySource("classpath:/connections/application-${profile}.properties")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
