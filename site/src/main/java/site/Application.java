package site;

import java.util.Arrays;

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
		SpringApplication application = new SpringApplication(Application.class);
		System.setProperty("spring.profiles.default", Profiles.DEV);
		if (Application.class.getResource("Application.class").getProtocol()
				.equals("file")) {
			if (System.getProperty("PROJECT_HOME") == null) {
				System.out.println("PROJECT_HOME sys prop not set");
				System.exit(1);
			}
			application.setAdditionalProfiles(Profiles.DEV);
			System.out.println("\n** Setting thymeleaf prefix to: "
					+ System.getProperty("PROJECT_HOME") + "/assets/public/\n");
			System.setProperty("spring.thymeleaf.prefix",
					"file:///" + System.getProperty("PROJECT_HOME")
							+ "/assets/public/");
		}
		String[] profiles = application.run(args).getEnvironment()
				.getActiveProfiles();
		System.out.println("Using profiles: " + Arrays.toString(profiles));
	}
}
