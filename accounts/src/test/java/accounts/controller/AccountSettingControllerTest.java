package accounts.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.AccountSettingsEnum;
import user.common.constants.RolesEnum;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.OrganizationRepository;
import accounts.repository.SocialRepository;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentService;
import accounts.service.UserService;

public class AccountSettingControllerTest {

	private AccountSettingsController accountSettingsController;

	private UserService userService = mock(UserService.class);

	private AccountSettingsService accountSettingsService = mock(AccountSettingsService.class);

	private DocumentService documentService = mock(DocumentService.class);

	private OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

	private SocialRepository socialRepository = mock(SocialRepository.class);

	@Before
	public void setUp() throws Exception {
		accountSettingsController = new AccountSettingsController(userService,
				accountSettingsService, documentService,
				organizationRepository, socialRepository);
	}

	@Test
	public void testAddFacebookConnected() {
		User user = mock(User.class);
		String network = AccountSettingsEnum.facebook.name();
		String settingName = AccountSettingsEnum.facebook_connected.name();

		List<AccountSetting> accountSettings = new ArrayList<>();

		when(socialRepository.isConnected(user, network)).thenReturn(true);

		accountSettingsController.addFacebookConnected(user, accountSettings);

		assertFalse(accountSettings.isEmpty());

		assertTrue(accountSettings.stream().allMatch(
				ac -> settingName.equals(ac.getName())
						&& "true".equals(ac.getValue())));

	}

	@Test
	public void testCreateUserRoleAccountSetting() {

		long userId = 52L;
		long organizationId = 28L;
		long auditUserId = 0L;

		UserOrganizationRole uor = new UserOrganizationRole(userId,
				organizationId, RolesEnum.STUDENT.name(), auditUserId);

		AccountSetting roleAccountSetting = accountSettingsController
				.createUserRoleAccountSetting(uor);

		assertNotNull(roleAccountSetting);
		assertTrue(RolesEnum.STUDENT.name().equals(
				roleAccountSetting.getValue()));
	}

	@Test
	public void testCreateUserOrganizationAccountSetting() {

		long userId = 52L;
		long organizationId = 28L;
		long auditUserId = 0L;
		String organizationName = "University1";
		Organization organization = new Organization(organizationId,
				organizationName);

		UserOrganizationRole uor = new UserOrganizationRole(userId,
				organizationId, RolesEnum.STUDENT.name(), auditUserId);

		when(organizationRepository.get(organizationId)).thenReturn(
				organization);

		AccountSetting roleAccountSetting = accountSettingsController
				.createUserOrganizationAccountSetting(uor);

		assertNotNull(roleAccountSetting);
		assertTrue(organizationName.equals(roleAccountSetting.getValue()));
	}

	@Test
	public void addUserOrganization() {
		List<AccountSetting> settings = new ArrayList<>();

		long userId = 52L;
		long organizationId = 28L;
		long auditUserId = 0L;
		String organizationName = "University1";

		UserOrganizationRole uor = new UserOrganizationRole(userId,
				organizationId, "role", auditUserId);

		Organization organization = new Organization(organizationId,
				organizationName);

		User user = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));

		when(organizationRepository.get(organizationId)).thenReturn(
				organization);

		accountSettingsController.addUserOrganization(user, settings);

		assertNotNull(settings);
		assertFalse(settings.isEmpty());
		assertNotNull(settings.get(0));
		assertTrue(organizationName.equals(settings.get(0).getValue()));
	}

	@Test
	public void testAddUserRole() {
		List<AccountSetting> settings = new ArrayList<>();

		long userId = 52L;
		long organizationId = 28L;
		long auditUserId = 0L;
		String role = RolesEnum.CAREER_ADVISOR.name();

		UserOrganizationRole uor = new UserOrganizationRole(userId,
				organizationId, role, auditUserId);

		User user = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));

		accountSettingsController.addUserRole(user, settings);

		assertNotNull(settings);
		assertFalse(settings.isEmpty());
		assertNotNull(settings.get(0));
		assertTrue(role.equals(settings.get(0).getValue()));
	}

	@Test
	public void testAddUserRolesAndOrganizations() {
		List<AccountSetting> settings = new ArrayList<>();

		long userId = 52L;
		long organizationId = 28L;
		long auditUserId = 0L;
		String organizationName = "University1";
		String role = RolesEnum.CAREER_ADVISOR.name();

		UserOrganizationRole uor = new UserOrganizationRole(userId,
				organizationId, role, auditUserId);

		Organization organization = new Organization(organizationId,
				organizationName);

		User user = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));

		when(organizationRepository.get(organizationId)).thenReturn(
				organization);

		accountSettingsController.addUserRolesAndOrganizations(user, settings);

		assertNotNull(settings);
		assertTrue(settings.size() == 2);
		assertNotNull(settings.get(0));
		assertNotNull(settings.get(1));
		assertTrue(settings.stream().anyMatch(as -> role.equals(as.getValue())));
		assertTrue(settings.stream().anyMatch(
				as -> organizationName.equals(as.getValue())));
	}

}
