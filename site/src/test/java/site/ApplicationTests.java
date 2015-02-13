package site;

import login.Application;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {
	// @Autowired
	// JdbcUserDetailsManager udm;
	// @Autowired
	// AuthenticationManagerBuilder amb;

	@BeforeClass
	public static void init() {
		System.setProperty("spring.thymeleaf.prefix",
				"file:///" + System.getProperty("PROJECT_HOME")
						+ "/assets/src/");
	}

	@Test
	public void contextLoads() {
	}

	// @Test(expected = UsernameNotFoundException.class)
	// public void testUserNotFound() {
	// udm.loadUserByUsername("invalidUser");
	// }
	//
	// @Test
	// public void testUserCreateLoad() {
	// User user = new User("test", "password",
	// Arrays.asList(new SimpleGrantedAuthority("TEST")));
	// udm.createUser(user);
	// UserDetails loadedUser = udm.loadUserByUsername("test");
	// assertNotNull(loadedUser);
	// assertEquals(user, loadedUser);
	// }
	//
	// @Test
	// public void testUserDelete() {
	// User user = new User("test-delete", "password",
	// Arrays.asList(new SimpleGrantedAuthority("TEST")));
	// udm.createUser(user);
	// assertTrue(udm.userExists("test-delete"));
	// udm.deleteUser("test-delete");
	// assertFalse(udm.userExists("test-delete"));
	// }
}
