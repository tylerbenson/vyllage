package accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.constants.Roles;
import accounts.model.OrganizationMember;
import accounts.model.User;
import accounts.model.UserRole;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserRoleRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LoginTest {

	@Autowired
	private UserDetailRepository repository;

	@Autowired
	private UserRoleRepository authRepo;

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
	public void userChangesPassword() {
		String userName = "changePassword";
		String oldPassword = "password";
		String newPassword = "newPassword";
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserRole(Roles.STUDENT.name().toUpperCase(),
						null)), Arrays.asList(organizationMember));

		repository.createUser(user);

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, oldPassword);

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		repository.changePassword(oldPassword, newPassword);

		User loadedUser = repository.loadUserByUsername(userName);

		Assert.assertNotNull(loadedUser);
		Assert.assertNotNull(loadedUser.getPassword());
		Assert.assertTrue(new BCryptPasswordEncoder().matches(newPassword,
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

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserRole(Roles.STUDENT.name().toUpperCase(),
						null)), Arrays.asList(organizationMember));

		repository.createUser(user);

		// remember the user is logged in using the link in the email
		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, oldPassword);

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		repository.changePassword(newPassword);

		User loadedUser = repository.loadUserByUsername(userName);

		Assert.assertNotNull(loadedUser);
		Assert.assertNotNull(loadedUser.getPassword());
		System.out.println(loadedUser.getUserId() + " "
				+ loadedUser.getPassword());
		Assert.assertTrue(new BCryptPasswordEncoder().matches(newPassword,
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

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserRole(Roles.STUDENT.name().toUpperCase(),
						null)), Arrays.asList(organizationMember));

		repository.createUser(user);

		repository.changePassword(oldPassword, newPassword);

		User loadedUser = repository.loadUserByUsername(userName);

		Assert.assertNotNull(loadedUser);
		Assert.assertNotNull(loadedUser.getPassword());
		Assert.assertTrue(new BCryptPasswordEncoder().matches(newPassword,
				loadedUser.getPassword()));
	}

	@Test
	public void testUserCreatedAndLoadsCorrectly() {
		String userName = "test";
		String oldPassword = "password";

		GrantedAuthority auth = new UserRole(
				Roles.STUDENT.name().toUpperCase(), null);

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth),
				Arrays.asList(organizationMember));

		repository.createUser(user);

		User loadedUser = repository.loadUserByUsername(userName);

		assertNotNull("User is null.", loadedUser);
		assertEquals("User is different.", user, loadedUser);
		assertTrue("Authorities not found.", loadedUser.getAuthorities()
				.contains(auth));
		Assert.assertTrue(new BCryptPasswordEncoder().matches(oldPassword,
				loadedUser.getPassword()));

		String userName2 = "test2";
		GrantedAuthority auth2 = new UserRole(Roles.STUDENT.name()
				.toUpperCase(), null);
		OrganizationMember organizationMember2 = new OrganizationMember(0L,
				null);

		User user2 = new User(userName2, oldPassword, enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked,
				Arrays.asList(auth2), Arrays.asList(organizationMember2));

		repository.createUser(user2);

		User loadedUser2 = repository.loadUserByUsername(userName2);

		assertNotNull("User is null.", loadedUser2);
		assertEquals("User is different.", user2, loadedUser2);
		assertTrue("Authorities not found.", loadedUser2.getAuthorities()
				.contains(auth));
		assertTrue("Organizations not found.", loadedUser2
				.getOrganizationMember().contains(organizationMember2));
		Assert.assertTrue(new BCryptPasswordEncoder().matches(oldPassword,
				loadedUser.getPassword()));
	}

	@Test
	public void testUserCreatedWithVeryLongPasswordAndLoadsCorrectly() {
		String userName = "long-password";
		String password = "This is my very long password I made up by myself. 123456";

		GrantedAuthority auth = new UserRole(
				Roles.STUDENT.name().toUpperCase(), null);

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth),
				Arrays.asList(organizationMember));

		repository.createUser(user);

		User loadedUser = repository.loadUserByUsername(userName);

		assertNotNull("User is null.", loadedUser);
		assertEquals("User is different.", user, loadedUser);
		assertTrue("Authorities not found.", loadedUser.getAuthorities()
				.contains(auth));
		assertTrue("Organizations not found.", loadedUser
				.getOrganizationMember().contains(organizationMember));
		Assert.assertTrue(new BCryptPasswordEncoder().matches(password,
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

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked,
				Arrays.asList(new UserRole(Roles.STUDENT.name().toUpperCase(),
						null)), Arrays.asList(organizationMember));

		repository.createUser(user);

		User loadUserByUsername = repository.loadUserByUsername(userName);

		assertNotNull(loadUserByUsername);

		repository.deleteUser(userName);

		accounts.model.User deletedUser = repository
				.loadUserByUsername(userName);

		assertTrue("User not found.", repository.userExists(userName));
		assertFalse("User is enabled.", deletedUser.isEnabled());

		assertTrue("Found Roles for user " + userName,
				authRepo.getByUserId(loadUserByUsername.getUserId()).isEmpty());

	}

	// turns out that the jooq inserts only when necessary, so it saves only one
	// Auth.
	// @Test(expected = UsernameNotFoundException.class)
	// public void
	// testUserCreateTransactionalFailsAndUserNotSavedAndAuthoritiesNotSaved() {
	// String userName = "test-transaction-create";
	// GrantedAuthority auth1 = new Authority("USER", userName);
	// GrantedAuthority auth2 = new Authority("USER", userName);
	//
	// User user = new User(userName, "password", Arrays.asList(auth1, auth2));
	//
	// repository.createUser(user);
	//
	// UserDetails loadedUser = repository.loadUserByUsername(userName);
	//
	// List<Authority> byUserName = authRepo.getByUserName(userName);
	// assertNull("User is not null.", loadedUser);
	// assertTrue("Authorities found, " + byUserName.size(),
	// byUserName.isEmpty());
	// }

	@Test
	public void testUserCreateDuplicateAuthoritySavesOnlyOne() {
		String userName = "test-duplicate-auth";
		String password = "password";
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		OrganizationMember organizationMember = new OrganizationMember(0L, null);

		GrantedAuthority auth1 = new UserRole(Roles.STUDENT.name()
				.toUpperCase(), null);
		GrantedAuthority auth2 = new UserRole(Roles.STUDENT.name()
				.toUpperCase(), null);

		User user = new User(userName, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth1,
						auth2), Arrays.asList(organizationMember));

		repository.createUser(user);

		User loadedUser = repository.loadUserByUsername(userName);

		List<UserRole> roles = authRepo.getByUserId(loadedUser.getUserId());
		assertTrue("Found more than 1 authority, "
				+ loadedUser.getAuthorities().size(), loadedUser
				.getAuthorities().size() == 1);
		assertTrue("Found more than 1 authority, " + roles.size(),
				roles.size() == 1);
	}

}
