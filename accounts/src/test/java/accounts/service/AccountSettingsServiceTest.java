package accounts.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jooq.tools.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import user.common.User;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.repository.AvatarRepository;

@RunWith(MockitoJUnitRunner.class)
public class AccountSettingsServiceTest {

	@Mock
	private AvatarRepository avatarRepository;

	@Mock
	private AccountSettingRepository accountSettingRepository;

	@Mock
	private UserService userService;

	@Test
	public void getUserNamesNull() {
		List<Long> userIds = Arrays.asList(1L);
		List<AccountNames> accountNames = Arrays.asList();

		when(userService.getNames(userIds)).thenReturn(accountNames);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNamesAndEmail(userIds);

		assertTrue(userNamesSettings.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getAccoutSettingSettingNameIsNull() {

		User user = Mockito.mock(User.class);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		String settingName = null;
		accountSettingsService.getAccountSetting(user, settingName);

	}

	@Test
	public void getUserNamesAllNull() {
		List<Long> userIds = Arrays.asList(1L);
		User names = Mockito.mock(User.class);
		List<User> accountNames = Arrays.asList(names);

		when(userService.getUsers(userIds)).thenReturn(accountNames);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNamesAndEmail(userIds);

		assertNotNull(userNamesSettings);

		assertFalse(userNamesSettings.stream().anyMatch(
				as -> as.getValue() != null));

		assertTrue(userNamesSettings.stream().anyMatch(
				as -> StringUtils.isEmpty(as.getValue())));
	}

	@Test
	public void getUserNamesSuccessfull() {
		List<Long> userIds = Arrays.asList(1L);
		User names = Mockito.mock(User.class);
		List<User> accountNames = Arrays.asList(names);

		when(userService.getUsers(userIds)).thenReturn(accountNames);
		when(names.getFirstName()).thenReturn("Mario");
		when(names.getMiddleName()).thenReturn(null);
		when(names.getLastName()).thenReturn("Luigi");

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNamesAndEmail(userIds);

		assertNotNull(userNamesSettings);

		assertTrue(userNamesSettings.stream().anyMatch(
				as -> names.getFirstName().equals(as.getValue())));

		assertTrue(userNamesSettings.stream().anyMatch(
				as -> names.getLastName().equals(as.getValue())));

		assertTrue(userNamesSettings.stream().anyMatch(
				as -> "Mario".equals(as.getValue())));

		assertTrue(userNamesSettings.stream().anyMatch(
				as -> "Luigi".equals(as.getValue())));

		assertTrue(userNamesSettings.stream()
				.filter(as -> "middleName".equals(as.getName()))
				.allMatch(as -> as.getValue() == null));
	}

	@Test
	public void getAccoutSettingFirstNameSuccessfull() {
		String settingName = "firstName";
		String firstName = "Zelda";

		User user = Mockito.mock(User.class);

		when(user.getFirstName()).thenReturn(firstName);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		assertNotNull(accountSetting);

		assertTrue(accountSetting.isPresent());

		assertEquals(firstName, accountSetting.get().getValue());

	}

	@Test
	public void getAccoutSettingMiddleNameSuccessfull() {
		String settingName = "middleName";
		String middleName = "Zelda";

		User user = Mockito.mock(User.class);

		when(user.getMiddleName()).thenReturn(middleName);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		assertNotNull(accountSetting);

		assertTrue(accountSetting.isPresent());

		assertEquals(middleName, accountSetting.get().getValue());

	}

	@Test
	public void getAccoutSettingLastNameSuccessfull() {
		String settingName = "lastName";
		String lastName = "Zelda";

		User user = Mockito.mock(User.class);

		when(user.getLastName()).thenReturn(lastName);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		assertNotNull(accountSetting);

		assertTrue(accountSetting.isPresent());

		assertEquals(lastName, accountSetting.get().getValue());

	}

	@Test
	public void setAccoutSettingSome() {
		String settingName = "some";
		String settingValue = "Zelda";
		Long userId = 5L;
		AccountSetting setting = new AccountSetting(null, userId, settingName,
				settingValue, Privacy.PUBLIC.name());

		User user = Mockito.mock(User.class);

		when(user.getUserId()).thenReturn(userId);

		when(accountSettingRepository.set(setting)).thenReturn(setting);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		assertNotNull(savedAccountSetting);

		assertEquals(settingValue, savedAccountSetting.getValue());
		assertEquals(userId, savedAccountSetting.getUserId());

	}

	@Test
	public void setAccoutSettingMiddleNameBlank() {
		String settingName = "middleName";
		String settingValue = "";
		Long userId = 5L;
		AccountSetting setting = new AccountSetting(null, userId, settingName,
				settingValue, Privacy.PUBLIC.name());

		User user = Mockito.mock(User.class);

		when(user.getUserId()).thenReturn(userId);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		assertNotNull(savedAccountSetting);

		assertEquals(settingValue, savedAccountSetting.getValue());
		assertEquals(userId, savedAccountSetting.getUserId());

	}

	@Test
	public void setAccoutSettingLastNameNull() {
		String settingName = "lastName";
		String settingValue = null;
		Long userId = 5L;
		AccountSetting setting = new AccountSetting(null, userId, settingName,
				settingValue, Privacy.PUBLIC.name());

		User user = Mockito.mock(User.class);

		when(user.getUserId()).thenReturn(userId);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository, avatarRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		assertNotNull(savedAccountSetting);

		assertEquals(settingValue, savedAccountSetting.getValue());
		assertEquals(userId, savedAccountSetting.getUserId());

	}
}
