package site;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ComponentScan.Filter;

@SpringBootApplication
@ComponentScan(basePackages = { "site" }, excludeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, value = { Application.class }) })
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
@PropertySource("classpath:/site/application-dev.properties")
public class ApplicationTestConfig implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
