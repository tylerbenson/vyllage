package site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { login.Application.class,
		profile.Application.class, Application.class })
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		if (Application.class.getResource("Application.class").getProtocol()
				.equals("file")) {
			System.setProperty("spring.profiles.active", Profiles.DEBUG);
			System.setProperty("spring.thymeleaf.prefix",
					"file:///" + System.getProperty("PROJECT_HOME")
							+ "/assets/src/");
		}
		SpringApplication.run(Application.class, args);
	}
}
