package accounts.repository;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.ApplicationTestConfig;
import accounts.model.Email;
import accounts.model.account.AccountNames;
import accounts.service.ConfirmationEmailService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserDetailRepositoryTest {

	@Inject
	private UserDetailRepository userDetailRepository;

	@Inject
	private DSLContext sql;

	@Inject
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	@Inject
	private UserCredentialsRepository credentialsRepository;

	@Inject
	private AccountSettingRepository accountSettingRepository;

	@Inject
	private DataSourceTransactionManager txManager;

	private ConfirmationEmailService confirmationEmailService = Mockito
			.mock(ConfirmationEmailService.class);

	// The method is deprecated to avoid others using it externally
	// It's ok to test.

	@SuppressWarnings("deprecation")
	@Test(expected = IllegalArgumentException.class)
	public void testNullUser() {
		userDetailRepository.createUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithForcePasswordChange() {
		userDetailRepository.createUser(null, false, false);
	}

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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

	@Test()
	public void testCreatesUserDoesNotSendConfirmationEmail() {
		UserDetailRepository repo = new UserDetailRepository(sql,
				confirmationEmailService, userOrganizationRoleRepository,
				credentialsRepository, accountSettingRepository, txManager);

		String username = "email4@google.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(username, "123456", enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(uor));

		repo.createUser(user, false, false);

		User loadUserByUsername = repo.loadUserByUsername(username);

		Assert.assertNotNull(loadUserByUsername);

		Assert.assertEquals(username, loadUserByUsername.getUsername());

		Mockito.verify(confirmationEmailService, Mockito.never())
				.sendConfirmationEmail(Mockito.any(User.class),
						Mockito.any(Email.class));

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
