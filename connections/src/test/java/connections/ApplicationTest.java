package connections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackages = { "connections" }, excludeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, value = { Application.class }) })
@PropertySource("classpath:/connections/application-dev.properties")
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
public class ApplicationTest {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
