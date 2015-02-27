package login;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("classpath:/login/application.properties")
public class Application implements CommandLineRunner {

	// @Autowired
	// private PersonRepository repository;

	@Override
	public void run(String... args) throws Exception {
		// System.err.println(this.repository.findAll());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}

// @Repository
// interface PersonRepository extends CrudRepository<Person, Long> {
//
// }
//
// @Entity
// class Person {
// @Id
// @GeneratedValue
// private Long id;
// private String firstName;
// private String lastName;
//
// public String getFirstName() {
// return this.firstName;
// }
//
// public void setFirstName(String firstName) {
// this.firstName = firstName;
// }
//
// public String getLastName() {
// return this.lastName;
// }
//
// public void setLastName(String lastname) {
// this.lastName = lastname;
// }
//
// @Override
// public String toString() {
// return "Person [firstName=" + this.firstName + ", lastName="
// + this.lastName + "]";
// }
// }