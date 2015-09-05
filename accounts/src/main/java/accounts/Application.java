package accounts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
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
