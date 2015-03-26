package site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackageClasses = { connections.Application.class,
		documents.Application.class, accounts.Application.class,
		Application.class })
@PropertySource("classpath:/connections/application.properties")
@PropertySource("classpath:/documents/application.properties")
@PropertySource("classpath:/accounts/application.properties")
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		if (Application.class.getResource("Application.class").getProtocol()
				.equals("file")) {
			// System.setProperty("spring.profiles.active", Profiles.DEBUG);
			String profile = System.getProperty("spring.profiles.active");
			if (profile != null && !profile.contentEquals("null"))
				System.out.println("Using profile: " + profile);
			else {
				System.setProperty("spring.profiles.active", Profiles.DEBUG);
				System.out.println("Using default profile.");
			}
			System.setProperty("spring.thymeleaf.prefix",
					"file:///" + System.getProperty("PROJECT_HOME")
							+ "/assets/public/");
		}
		SpringApplication.run(Application.class, args);
	}
}
