package site;

import java.util.Arrays;
import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = { connections.Application.class,
		documents.Application.class, accounts.Application.class,
		Application.class })
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
public class Application implements CommandLineRunner {
	private static final Logger logger = Logger.getLogger(Application.class
			.getName());

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);

		// this seems to be ignored
		System.setProperty("spring.profiles.default", Profiles.DEV);

		if (Application.class.getResource("Application.class").getProtocol()
				.equals("file")) {
			if (System.getProperty("PROJECT_HOME") == null) {
				logger.info("PROJECT_HOME sys prop not set");
				System.exit(1);
			}

			// this breaks prod
			// application.setAdditionalProfiles(Profiles.DEV);
			logger.info("\n** Setting thymeleaf prefix to: "
					+ System.getProperty("PROJECT_HOME") + "/assets/public/\n");
			System.setProperty("spring.thymeleaf.prefix",
					"file:///" + System.getProperty("PROJECT_HOME")
							+ "/assets/public/");
		}

		String[] profiles = application.run(args).getEnvironment()
				.getActiveProfiles();

		logger.info("Using profiles: " + Arrays.toString(profiles));

	}

	@Override
	public void run(String... args) throws Exception {
	}
}
