package accounts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
// @ComponentScan(basePackages = { "accounts" }, excludeFilters = { @Filter(type
// = FilterType.ASSIGNABLE_TYPE, value = MockBeansConfiguration.class) })
@PropertySource("classpath:/accounts/application.properties")
public class Application implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
