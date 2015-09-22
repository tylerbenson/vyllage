package documents;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackages = { "documents" }, excludeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
		BeansConfiguration.class, Application.class }) })
@PropertySource("classpath:/documents/application.properties")
public class ApplicationTestConfig implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
