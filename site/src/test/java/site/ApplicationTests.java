package site;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class ApplicationTests {

	@Inject
	private Environment env;

	@BeforeClass
	public static void init() {
		System.setProperty("spring.thymeleaf.prefix",
				"file:///" + System.getProperty("PROJECT_HOME")
						+ "/assets/src/");
	}

	@Test
	public void contextLoads() {
		System.out.println(env.getRequiredProperty("jooq.sql.dialect"));
	}
}
