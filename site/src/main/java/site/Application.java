package site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackageClasses = { connections.Application.class,
		documents.Application.class, accounts.Application.class,
		Application.class })
@PropertySource("classpath:/connections/application.properties")
@PropertySource("classpath:/documents/application.properties")
@PropertySource("classpath:/accounts/application.properties")
public class Application {

	public static void main(String[] args) {
		if (Application.class.getResource("Application.class").getProtocol()
				.equals("file")) {
			// System.setProperty("spring.profiles.active", Profiles.DEBUG);
			String profile = System.getProperty("spring.profiles.active");
			if (profile != null && !profile.contentEquals("null"))
				System.out.println("Using profile: " + profile);
			else {
				profile = profile == null ? Profiles.DEV : profile;
				// System.setProperty("spring.profiles.active", Profiles.DEV);
				System.out.println("Using " + profile + " profile.");
			}
			System.setProperty("spring.thymeleaf.prefix",
					"file:///" + System.getProperty("PROJECT_HOME")
							+ "/assets/public/");
		}
		SpringApplication.run(Application.class, args);
	}
}
