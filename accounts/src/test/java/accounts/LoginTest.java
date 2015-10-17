package accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.repository.PasswordResetWasForcedException;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserOrganizationRoleRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class LoginTest {

	private static final boolean FORCE_PASSWORD_CHANGE = false;
	private static final boolean SEND_CONFIRMATION_EMAIL = false;

	@Inject
	private UserDetailRepository userDetailRepository;

	@Inject
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	@Test
	public void userExistsTest() {
		String username = "mario@toadstool.com";
		UserDetails loadUserByUsername = userDetailRepository
				.loadUserByUsername(username);

		assertNotNull(loadUserByUsername);
		assertEquals(username, loadUserByUsername.getUsername());
		assertNotNull(loadUserByUsername.getPassword());
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testUserNotFound() {
		userDetailRepository.loadUserByUsername("invalidUser");
	}

	@Test(expected = PasswordResetWasForcedException.class)
	public void testFirstLogin() {
		String userName = "firstLogin";
		String password = "password";
		boolean forcePasswordChange = true;
		boolean enabled = forcePasswordChange;
		boolean accountNonExpired = forcePasswordChange;
		boolean credentialsNonExpired = forcePasswordChange;
		boolean accountNonLocked = forcePasswordChange;

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserOrganizationRole(null, 0L,
						RolesEnum.STUDENT.name().toUpperCase(), 0L)));

		userDetailRepository.createUser(user, forcePasswordChange,
				SEND_CONFIRMATION_EMAIL);

		userDetailRepository.loadUserByUsername(userName);
	}

	@Test
	public void userChangesPassword() {
		String userName = "changePassword";
		String oldPassword = "password";
		String newPassword = "newPassword";
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserOrganizationRole(null, 0L,
						RolesEnum.STUDENT.name().toUpperCase(), 0L)));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, oldPassword);

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		userDetailRepository.changePassword(oldPassword, newPassword);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		assertNotNull(loadedUser);
		assertNotNull(loadedUser.getPassword());
		assertTrue(new BCryptPasswordEncoder().matches(newPassword,
				loadedUser.getPassword()));
	}

	@Test
	public void userResetsPassword() {
		String userName = "resetPassword";
		String oldPassword = "password";
		String newPassword = "newPassword";
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserOrganizationRole(null, 0L,
						RolesEnum.STUDENT.name().toUpperCase(), 0L)));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		// remember the user is logged in using the link in the email
		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, oldPassword);

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		userDetailRepository.changePassword(newPassword);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		assertNotNull(loadedUser);
		assertNotNull(loadedUser.getPassword());
		assertTrue(new BCryptPasswordEncoder().matches(newPassword,
				loadedUser.getPassword()));
	}

	@Test(expected = AccessDeniedException.class)
	public void userChangesPasswordNotLoggedInTest() {
		String userName = "changePassword";
		String oldPassword = "password";
		String newPassword = "newPassword";
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserOrganizationRole(null, 0L,
						RolesEnum.STUDENT.name().toUpperCase(), 0L)));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		userDetailRepository.changePassword(oldPassword, newPassword);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		assertNotNull(loadedUser);
		assertNotNull(loadedUser.getPassword());
		assertTrue(new BCryptPasswordEncoder().matches(newPassword,
				loadedUser.getPassword()));
	}

	@Test
	public void testUserCreatedAndLoadsCorrectly() {
		String userName = "test";
		String oldPassword = "password";

		UserOrganizationRole auth = new UserOrganizationRole(null, 0L,
				RolesEnum.STUDENT.name().toUpperCase(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);
		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		assertNotNull("User is null.", loadedUser);
		assertEquals("User is different.", user, loadedUser);
		assertTrue("Authorities not found.", loadedUser.getAuthorities()
				.contains(auth));
		assertTrue(new BCryptPasswordEncoder().matches(oldPassword,
				loadedUser.getPassword()));

		String userName2 = "test2";
		UserOrganizationRole auth2 = new UserOrganizationRole(null, 0L,
				RolesEnum.STUDENT.name().toUpperCase(), 0L);

		User user2 = new User(userName2, oldPassword, enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked,
				Arrays.asList(auth2));

		userDetailRepository.createUser(user2, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		User loadedUser2 = userDetailRepository.loadUserByUsername(userName2);

		assertNotNull("User is null.", loadedUser2);
		assertEquals("User is different.", user2, loadedUser2);
		assertTrue("Authorities not found.", loadedUser2.getAuthorities()
				.contains(auth));
		assertTrue(new BCryptPasswordEncoder().matches(oldPassword,
				loadedUser.getPassword()));
	}

	@Test
	public void testUserCreatedWithVeryLongPasswordAndLoadsCorrectly() {
		String userName = "long-password";
		String password = "This is my very long password I made up by myself. 123456";

		UserOrganizationRole auth = new UserOrganizationRole(null, 0L,
				RolesEnum.STUDENT.name().toUpperCase(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		assertNotNull("User is null.", loadedUser);
		assertEquals("User is different.", user, loadedUser);
		assertTrue("Authorities not found.", loadedUser.getAuthorities()
				.contains(auth));
		assertTrue(new BCryptPasswordEncoder().matches(password,
				loadedUser.getPassword()));

	}

	@Test
	public void testUserDelete() {
		String userName = "test-delete";
		String password = "password";

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserOrganizationRole(null, 0L,
						RolesEnum.STUDENT.name().toUpperCase(), 0L)));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		User loadUserByUsername = userDetailRepository
				.loadUserByUsername(userName);

		assertNotNull(loadUserByUsername);

		userDetailRepository.deleteUser(userName);

		User deletedUser = userDetailRepository.loadUserByUsername(userName);

		assertTrue("User not found.", userDetailRepository.userExists(userName));
		assertFalse("User is enabled.", deletedUser.isEnabled());

		assertTrue(
				"Found Roles for user " + userName,
				userOrganizationRoleRepository.getByUserId(
						loadUserByUsername.getUserId()).isEmpty());

	}

	@Test
	public void testUserCreateDuplicateAuthoritySavesOnlyOne() {
		String userName = "test-duplicate-auth";
		String password = "password";
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		UserOrganizationRole auth1 = new UserOrganizationRole(null, 0L,
				RolesEnum.STUDENT.name().toUpperCase(), 0L);
		UserOrganizationRole auth2 = new UserOrganizationRole(null, 0L,
				RolesEnum.STUDENT.name().toUpperCase(), 0L);

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth1,
						auth2));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE,
				SEND_CONFIRMATION_EMAIL);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		List<UserOrganizationRole> roles = userOrganizationRoleRepository
				.getByUserId(loadedUser.getUserId());
		assertTrue("Found more than 1 authority, "
				+ loadedUser.getAuthorities().size(), loadedUser
				.getAuthorities().size() == 1);
		assertTrue("Found more than 1 authority, " + roles.size(),
				roles.size() == 1);
	}

}
