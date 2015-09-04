package accounts.repository;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.Application;
import accounts.mocks.MockTextEncryptor;
import accounts.mocks.SelfReturningAnswer;
import accounts.model.account.AccountNames;
import accounts.service.ConfirmationEmailService;

import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserDetailRepositoryTest {

	private UserDetailRepository userDetailRepository;

	private ConfirmationEmailService confirmationEmailService;

	@Inject
	private DSLContext sql;

	@Inject
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	@Inject
	private OrganizationRepository organizationRepository;

	@Inject
	private UserCredentialsRepository credentialsRepository;

	@Inject
	private AccountSettingRepository accountSettingRepository;

	@Inject
	private DataSourceTransactionManager txManager;

	private Environment environment = mock(Environment.class);

	private EmailBuilder emailBuilder = mock(EmailBuilder.class,
			new SelfReturningAnswer());

	private EmailRepository emailRepository = mock(EmailRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	private TextEncryptor encryptor = new MockTextEncryptor();

	@Before
	public void setUp() {
		confirmationEmailService = new ConfirmationEmailService(environment,
				emailBuilder, mapper, encryptor, emailRepository);

		userDetailRepository = new UserDetailRepository(sql,
				userOrganizationRoleRepository, organizationRepository,
				credentialsRepository, accountSettingRepository, txManager,
				confirmationEmailService);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullUser() {
		userDetailRepository.createUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
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
