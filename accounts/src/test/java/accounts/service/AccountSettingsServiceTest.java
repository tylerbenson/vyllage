package accounts.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import accounts.Application;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.repository.ElementNotFoundException;
import accounts.repository.UserNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AccountSettingsServiceTest {

	@Inject
	private AccountSettingsService accountSettingsService;

	@Inject
	private UserService userService;

	@Inject
	private AccountSettingRepository accountSettingRepository;

	@Test
	public void saveAccountSetting() {
		Long userId = 1L;
		String emailUpdates = "emailUpdates";
		String value = "NEVER";

		User u = Mockito.mock(User.class);
		AccountSetting as = new AccountSetting();
		as.setName(emailUpdates);
		as.setUserId(userId);
		as.setValue(value);
		as.setPrivacy(Privacy.PUBLIC.name());

		Mockito.when(u.getUserId()).thenReturn(userId);

		accountSettingsService.setAccountSetting(u, as);

		Optional<AccountSetting> setting = accountSettingsService
				.getAccountSetting(u, emailUpdates);
		Assert.assertTrue(setting.isPresent());
		Assert.assertEquals(emailUpdates, setting.get().getName());
		Assert.assertEquals(value, setting.get().getValue());
	}

	@Test
	public void saveAccountSettingFirstName() throws UserNotFoundException,
			ElementNotFoundException {
		Long userId = 1L;
		String fieldName = "firstName";
		String value = "Demogorgon";

		User u = userService.getUser(userId);
		AccountSetting as = new AccountSetting();
		as.setName(fieldName);
		as.setUserId(userId);
		as.setValue(value);
		as.setPrivacy(Privacy.PUBLIC.name());

		accountSettingsService.setAccountSetting(u, as);

		Optional<AccountSetting> savedSetting = accountSettingsService
				.getAccountSetting(u, fieldName);
		Assert.assertTrue(savedSetting.isPresent());
		Assert.assertEquals(fieldName, savedSetting.get().getName());
		Assert.assertEquals(value, savedSetting.get().getValue());

		User u2 = userService.getUser(userId);
		Assert.assertEquals(value, u2.getFirstName());
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void saveAccountSettingWrongId() {
		Long userId = -1L;
		AccountSetting as = new AccountSetting();
		User u = Mockito.mock(User.class);

		Mockito.when(u.getUserId()).thenReturn(userId);
		as.setName("data-integrity");
		as.setUserId(userId);
		as.setValue("data-integrity");
		as.setPrivacy(Privacy.PUBLIC.name());

		accountSettingsService.setAccountSetting(u, as);
	}

	@Test
	public void getUserNamesSuccessfull() {
		List<Long> userIds = Arrays.asList(1L);
		AccountNames names = new AccountNames(1L, "Mario", null, "Mario");
		List<AccountNames> accountNames = Arrays.asList(names);

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		Mockito.when(userService.getNames(userIds)).thenReturn(accountNames);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNames(userIds);

		Assert.assertNotNull(userNamesSettings);

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> names.getFirstName().equals(as.getValue())));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> names.getLastName().equals(as.getValue())));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> as.getValue() == null));
	}

	@Test
	public void getUserNamesAllNull() {
		List<Long> userIds = Arrays.asList(1L);
		AccountNames names = new AccountNames(1L, null, null, null);
		List<AccountNames> accountNames = Arrays.asList(names);

		AccountSettingRepository accountSettingRepository = Mockito
				.mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		Mockito.when(userService.getNames(userIds)).thenReturn(accountNames);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		List<AccountSetting> userNamesSettings = accountSettingsService
				.getUserNames(userIds);

		Assert.assertNotNull(userNamesSettings);

		Assert.assertFalse(userNamesSettings.stream().anyMatch(
				as -> as.getValue() != null));

		Assert.assertTrue(userNamesSettings.stream().anyMatch(
				as -> as.getValue() == null));
	}

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
				.getUserNames(userIds);

		Assert.assertTrue(userNamesSettings.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
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
	public void getAccoutSettingEmailSuccessfull() throws UserNotFoundException {
		String settingName = "email";

		Long userId = 0L;
		User user = userService.getUser(userId);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		Assert.assertNotNull(accountSetting);

		Assert.assertTrue(accountSetting.isPresent());

		Assert.assertEquals(user.getUsername(), accountSetting.get().getValue());

	}

	@Test
	public void setAccoutSettingEmailShouldNotChange()
			throws UserNotFoundException {
		String settingName = "email";
		String settingValue = "new@gmail.com";

		Long userId = 0L;
		User user = userService.getUser(userId);

		AccountSetting accountSetting = new AccountSetting(null, userId,
				"email", settingValue, Privacy.PUBLIC.name().toLowerCase());

		accountSettingsService.setAccountSetting(user, accountSetting);

		Optional<AccountSetting> accountSetting2 = accountSettingsService
				.getAccountSetting(user, settingName);

		Assert.assertNotNull(accountSetting2);

		Assert.assertTrue(accountSetting2.isPresent());

		Assert.assertEquals(user.getUsername(), accountSetting2.get()
				.getValue());

	}

	@Test
	public void getAllAccoutSettingsByUserIdSuccessfull()
			throws UserNotFoundException {
		Long userId = 0L;

		List<Long> userIds = Arrays.asList(userId);
		List<AccountSetting> accountSetting = accountSettingsService
				.getAccountSettings(userIds);

		Assert.assertNotNull(accountSetting);
		Assert.assertFalse(accountSetting.isEmpty());
	}

	@Test
	public void getAllAccoutSettingsSuccessfull() throws UserNotFoundException {
		Long userId = 0L;
		User user = userService.getUser(userId);

		List<AccountSetting> accountSetting = accountSettingsService
				.getAccountSettings(user);

		Assert.assertNotNull(accountSetting);
		Assert.assertFalse(accountSetting.isEmpty());
	}

	@Test
	public void getAccoutSettingNotFound() {
		String settingName = "email";

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(999999999999L);

		Assert.assertFalse(accountSettingsService.getAccountSetting(user,
				settingName).isPresent());

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

	@Test
	public void setAccoutSettingEmailSuccessfull() {
		String settingName = "email";
		String settingValue = "some@email.com";
		String previousEmail = "old@email.com";
		Long userId = 3L;
		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		// AccountSettingRepository accountSettingRepository = Mockito
		// .mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUsername()).thenReturn(previousEmail);
		Mockito.when(user.getUserId()).thenReturn(userId);
		// Mockito.when(accountSettingRepository.set(userId,
		// setting)).thenReturn(
		// setting);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, accountSettingRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		Assert.assertNotNull(savedAccountSetting);

		// email can only be changed after confirmation, we return the previous
		// value until then
		Assert.assertEquals(previousEmail, savedAccountSetting.getValue());
		Assert.assertEquals(userId, savedAccountSetting.getUserId());

		Optional<AccountSetting> newEmailSetting = accountSettingsService
				.getAccountSetting(user, "newEmail");

		Assert.assertTrue(newEmailSetting.isPresent());

		Assert.assertEquals(settingValue, newEmailSetting.get().getValue());

	}

	@Test(expected = DataIntegrityViolationException.class)
	public void setAccoutSettingNull() throws UserNotFoundException {
		String settingName = "someSetting";
		String settingValue = null;
		Long userId = 1L;
		User user = userService.getUser(userId);

		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		accountSettingsService.setAccountSetting(user, setting);

	}

	@Test(expected = IllegalArgumentException.class)
	public void setAccoutSettingEmailEmptyDoesNotChangeEmail()
			throws UserNotFoundException {
		String settingName = "email";
		String settingValue = "";
		Long userId = 1L;
		User user = userService.getUser(userId);

		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		accountSettingsService.setAccountSetting(user, setting);
	}

	@Test
	public void setAccoutSettings() {
		String settingName = "someSetting";
		String settingValue = "some@email.com";
		Long userId = 1L;

		AccountSetting setting = new AccountSetting(null, null, settingName,
				settingValue, Privacy.PUBLIC.name());

		AccountSetting org = new AccountSetting(null, null, "organization",
				"organization", Privacy.PUBLIC.name());

		AccountSetting role = new AccountSetting(null, null, "role", "role",
				Privacy.PUBLIC.name());

		List<AccountSetting> settings = Arrays.asList(setting, org, role);

		User user = Mockito.mock(User.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		List<AccountSetting> savedAccountSettings = accountSettingsService
				.setAccountSettings(user, settings);

		savedAccountSettings.forEach(System.out::println);

		Assert.assertNotNull(savedAccountSettings);
		Assert.assertTrue(savedAccountSettings.contains(setting));
		Assert.assertFalse(savedAccountSettings.contains(org));
		Assert.assertFalse(savedAccountSettings.contains(role));

	}
}
