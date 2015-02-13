package login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import login.repository.UserDetailRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LoginTest {

	@Autowired
	private UserDetailRepository repository;

	@Test
	public void userExistsTest() {
		String username = "email";
		UserDetails loadUserByUsername = repository
				.loadUserByUsername(username);

		Assert.assertNotNull(loadUserByUsername);
		Assert.assertEquals(username, loadUserByUsername.getUsername());
		Assert.assertNotNull(loadUserByUsername.getPassword());
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testUserNotFound() {
		repository.loadUserByUsername("invalidUser");
	}

	@Test
	public void testUserCreateLoad() {
		User user = new User("test", "password",
				Arrays.asList(new SimpleGrantedAuthority("TEST")));

		repository.createUser(user);

		UserDetails loadedUser = repository.loadUserByUsername("test");

		assertNotNull(loadedUser);
		assertEquals(user, loadedUser);
	}

	@Test
	public void testUserDelete() {
		User user = new User("test-delete", "password",
				Arrays.asList(new SimpleGrantedAuthority("TEST")));

		repository.createUser(user);

		assertTrue(repository.userExists("test-delete"));

		repository.deleteUser("test-delete");

		assertFalse(repository.userExists("test-delete"));

	}

}
