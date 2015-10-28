package accounts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import util.profiles.Profiles;
import accounts.config.BeansConfiguration;

@Profile(Profiles.TEST)
@SpringBootApplication
@ComponentScan(basePackages = { "accounts" }, excludeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
		BeansConfiguration.class, Application.class }) })
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
@PropertySource("classpath:/accounts/application-test.properties")
public class ApplicationTestConfig implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
	}

	public static void main(String[] args) {
		System.setProperty("spring.thymeleaf.prefix",
				"file:///" + System.getProperty("PROJECT_HOME")
						+ "/assets/src/");

		SpringApplication.run(Application.class, args);
	}
}
