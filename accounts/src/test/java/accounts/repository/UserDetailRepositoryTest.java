package accounts.repository;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.Application;
import accounts.model.account.AccountNames;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserDetailRepositoryTest {

	@Inject
	UserDetailRepository userDetailRepository;

	@Test(expected = NullPointerException.class)
	public void testNullUser() {
		userDetailRepository.createUser(null);
	}

	@Test(expected = NullPointerException.class)
	public void testWithForcePasswordChange() {
		userDetailRepository.createUser(null, false);
	}

	@Test(expected = PasswordResetWasForcedException.class)
	public void testCreateUserThrowsPasswordResetException() {
		String username = "email1@google.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(username, "123456", enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(uor));

		userDetailRepository.createUser(user);

		userDetailRepository.loadUserByUsername(username);

	}

	@Test()
	public void testCreateUser() {
		String username = "email2@google.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(username, "123456", enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(uor));

		userDetailRepository.createUser(user);

		userDetailRepository.setForcePasswordReset(username, false);

		user.common.User loadUserByUsername = userDetailRepository
				.loadUserByUsername(username);

		Assert.assertNotNull(loadUserByUsername);

		Assert.assertEquals(username, loadUserByUsername.getUsername());

	}

	@Test()
	public void testDisableUser() {
		String username = "email3@google.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(username, "123456", enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(uor));

		userDetailRepository.createUser(user);

		userDetailRepository.setForcePasswordReset(username, false);

		User loadUserByUsername = userDetailRepository
				.loadUserByUsername(username);

		Assert.assertNotNull(loadUserByUsername);

		Assert.assertTrue(loadUserByUsername.isEnabled());

		userDetailRepository.enableDisableUser(loadUserByUsername.getUserId());

		user.common.User loadDisabledUserByUsername = userDetailRepository
				.loadUserByUsername(username);

		Assert.assertFalse(loadDisabledUserByUsername.isEnabled());
	}

	@Test
	public void testGetUserNames() {
		List<AccountNames> names = userDetailRepository.getNames(Arrays
				.asList(0L));

		Assert.assertNotNull(names);
		Assert.assertTrue(names.stream().anyMatch(
				u -> "Luke".equals(u.getFirstName())));
	}

}
