package accounts.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jooq.tools.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import user.common.User;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;

public class AccountSettingsServiceTest {

	@Test
	public void getUserNamesNull() {
		List<Long> userIds = Arrays.asList(1L);
		List<AccountNames> accountNames = Arrays.asList();

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		Mockito.when(userService.getNames(userIds)).thenReturn(accountNames);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNamesAndEmail(userIds);

		Assert.assertTrue(userNamesSettings.isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void getAccoutSettingSettingNameIsNull() {

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		String settingName = null;
		accountSettingsService.getAccountSetting(user, settingName);

	}

	@Test
	public void getUserNamesAllNull() {
		List<Long> userIds = Arrays.asList(1L);
		User names = Mockito.mock(User.class);
		List<User> accountNames = Arrays.asList(names);

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		Mockito.when(userService.getUsers(userIds)).thenReturn(accountNames);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNamesAndEmail(userIds);

		Assert.assertNotNull(userNamesSettings);

		Assert.assertFalse(userNamesSettings.stream().anyMatch(
				as -> as.getValue() != null));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> StringUtils.isEmpty(as.getValue())));
	}

	@Test
	public void getUserNamesSuccessfull() {
		List<Long> userIds = Arrays.asList(1L);
		User names = Mockito.mock(User.class);
		List<User> accountNames = Arrays.asList(names);

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		Mockito.when(userService.getUsers(userIds)).thenReturn(accountNames);
		Mockito.when(names.getFirstName()).thenReturn("Mario");
		Mockito.when(names.getMiddleName()).thenReturn(null);
		Mockito.when(names.getLastName()).thenReturn("Luigi");

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNamesAndEmail(userIds);

		Assert.assertNotNull(userNamesSettings);

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> names.getFirstName().equals(as.getValue())));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> names.getLastName().equals(as.getValue())));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> "Mario".equals(as.getValue())));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> "Luigi".equals(as.getValue())));

		Assert.assertTrue(userNamesSettings.stream()
				.filter(as -> "middleName".equals(as.getName()))
				.allMatch(as -> as.getValue() == null));
	}

	@Test
	public void getAccoutSettingFirstNameSuccessfull() {
		String settingName = "firstName";
		String firstName = "Zelda";

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getFirstName()).thenReturn(firstName);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		Assert.assertNotNull(accountSetting);

		Assert.assertTrue(accountSetting.isPresent());

		Assert.assertEquals(firstName, accountSetting.get().getValue());

	}

	@Test
	public void getAccoutSettingMiddleNameSuccessfull() {
		String settingName = "middleName";
		String middleName = "Zelda";

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getMiddleName()).thenReturn(middleName);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		Assert.assertNotNull(accountSetting);

		Assert.assertTrue(accountSetting.isPresent());

		Assert.assertEquals(middleName, accountSetting.get().getValue());

	}

	@Test
	public void getAccoutSettingLastNameSuccessfull() {
		String settingName = "lastName";
		String lastName = "Zelda";

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getLastName()).thenReturn(lastName);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		Assert.assertNotNull(accountSetting);

		Assert.assertTrue(accountSetting.isPresent());

		Assert.assertEquals(lastName, accountSetting.get().getValue());

	}

	@Test
	public void setAccoutSettingSome() {
		String settingName = "some";
		String settingValue = "Zelda";
		Long userId = 5L;
		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		Mockito.when(accountSettingRepository.set(userId, setting)).thenReturn(
				setting);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		Assert.assertNotNull(savedAccountSetting);

		Assert.assertEquals(settingValue, savedAccountSetting.getValue());
		Assert.assertEquals(userId, savedAccountSetting.getUserId());

	}

	@Test
	public void setAccoutSettingMiddleNameBlank() {
		String settingName = "middleName";
		String settingValue = "";
		Long userId = 5L;
		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		Assert.assertNotNull(savedAccountSetting);

		Assert.assertEquals(settingValue, savedAccountSetting.getValue());
		Assert.assertEquals(userId, savedAccountSetting.getUserId());

	}

	@Test
	public void setAccoutSettingLastNameNull() {
		String settingName = "lastName";
		String settingValue = null;
		Long userId = 5L;
		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		Assert.assertNotNull(savedAccountSetting);

		Assert.assertEquals(settingValue, savedAccountSetting.getValue());
		Assert.assertEquals(userId, savedAccountSetting.getUserId());

	}
}
