package accounts.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
import user.common.constants.AccountSettingsEnum;
import accounts.ApplicationTestConfig;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.repository.AvatarRepository;
import accounts.repository.ElementNotFoundException;
import accounts.repository.UserNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AccountSettingsServiceIntegrationTest {

	@Inject
	private AccountSettingsService accountSettingsService;

	@Inject
	private UserService userService;

	@Inject
	private AccountSettingRepository accountSettingRepository;

	@Inject
	private AvatarRepository avatarRepository;

	@Inject
	private DocumentService documentService;

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

		when(u.getUserId()).thenReturn(userId);

		accountSettingsService.setAccountSetting(u, as);

		Optional<AccountSetting> setting = accountSettingsService
				.getAccountSetting(u, emailUpdates);
		assertTrue(setting.isPresent());
		assertEquals(emailUpdates, setting.get().getName());
		assertEquals(value, setting.get().getValue());
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
		assertTrue(savedSetting.isPresent());
		assertEquals(fieldName, savedSetting.get().getName());
		assertEquals(value, savedSetting.get().getValue());

		User u2 = userService.getUser(userId);
		assertEquals(value, u2.getFirstName());
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void saveAccountSettingWrongId() {
		Long userId = -1L;
		AccountSetting as = new AccountSetting();
		User u = Mockito.mock(User.class);

		when(u.getUserId()).thenReturn(userId);
		as.setName("data-integrity");
		as.setUserId(userId);
		as.setValue("data-integrity");
		as.setPrivacy(Privacy.PUBLIC.name());

		accountSettingsService.setAccountSetting(u, as);
	}

	@Test
	public void getAccoutSettingEmailSuccessfull() throws UserNotFoundException {
		String settingName = "email";

		Long userId = 0L;
		User user = userService.getUser(userId);

		Optional<AccountSetting> accountSetting = accountSettingsService
				.getAccountSetting(user, settingName);

		assertNotNull(accountSetting);

		assertTrue(accountSetting.isPresent());

		assertEquals(user.getUsername(), accountSetting.get().getValue());

	}

	@Test
	public void setAccoutSettingEmailShouldNotChange()
			throws UserNotFoundException {
		String settingName = "email";
		String settingValue = "user@vyllage.com";

		Long userId = 0L;
		User user = userService.getUser(userId);

		AccountSetting accountSetting = new AccountSetting(null, userId,
				"email", settingValue, Privacy.PUBLIC.name().toLowerCase());

		accountSettingsService.setAccountSetting(user, accountSetting);

		Optional<AccountSetting> accountSetting2 = accountSettingsService
				.getAccountSetting(user, settingName);

		assertNotNull(accountSetting2);

		assertTrue(accountSetting2.isPresent());

		assertEquals(user.getUsername(), accountSetting2.get().getValue());

	}

	@Test
	public void getAllAccoutSettingsByUserIdSuccessfull()
			throws UserNotFoundException {
		Long userId = 0L;

		List<Long> userIds = Arrays.asList(userId);
		List<AccountSetting> accountSetting = accountSettingsService
				.getAccountSettings(userIds);

		assertNotNull(accountSetting);
		assertFalse(accountSetting.isEmpty());
	}

	@Test
	public void getAllAccoutSettingsSuccessfull() throws UserNotFoundException {
		Long userId = 0L;
		User user = userService.getUser(userId);

		List<AccountSetting> accountSetting = accountSettingsService
				.getAccountSettings(user);

		assertNotNull(accountSetting);
		assertFalse(accountSetting.isEmpty());
	}

	@Test
	public void getAccoutSettingNotFound() {
		String settingName = "a";

		User user = Mockito.mock(User.class);

		when(user.getUserId()).thenReturn(999999999999L);

		assertFalse(accountSettingsService.getAccountSetting(user, settingName)
				.isPresent());

	}

	@Test
	public void setAccoutSettingEmailSuccessfull() {
		String settingName = "email";
		String settingValue = "some@email.com";
		String previousEmail = "old@email.com";
		Long userId = 3L;
		AccountSetting setting = new AccountSetting(null, userId, settingName,
				settingValue, Privacy.PUBLIC.name());

		// AccountSettingRepository accountSettingRepository = Mockito
		// .mock(AccountSettingRepository.class);
		UserService userService = Mockito.mock(UserService.class);

		User user = Mockito.mock(User.class);

		when(user.getUsername()).thenReturn(previousEmail);
		when(user.getUserId()).thenReturn(userId);
		// when(accountSettingRepository.set(userId,
		// setting)).thenReturn(
		// setting);

		AccountSettingsService accountSettingsService = new AccountSettingsService(
				userService, documentService, accountSettingRepository,
				avatarRepository);

		AccountSetting savedAccountSetting = accountSettingsService
				.setAccountSetting(user, setting);

		assertNotNull(savedAccountSetting);

		// email can only be changed after confirmation, we return the previous
		// value until then
		assertEquals(previousEmail, savedAccountSetting.getValue());
		assertEquals(userId, savedAccountSetting.getUserId());

		Optional<AccountSetting> newEmailSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.newEmail.name());

		assertTrue(newEmailSetting.isPresent());

		assertEquals(settingValue, newEmailSetting.get().getValue());

	}

	@Test(expected = DataIntegrityViolationException.class)
	public void setAccoutSettingNull() throws UserNotFoundException {
		String settingName = "someSetting";
		String settingValue = null;
		Long userId = 1L;
		User user = userService.getUser(userId);

		AccountSetting setting = new AccountSetting(null, userId, settingName,
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

		AccountSetting setting = new AccountSetting(null, userId, settingName,
				settingValue, Privacy.PUBLIC.name());

		accountSettingsService.setAccountSetting(user, setting);
	}

	@Test
	public void setAccoutSettings() {
		String settingName = "someSetting";
		String settingValue = "some@email.com";
		Long userId = 1L;

		AccountSetting setting = new AccountSetting(null, userId, settingName,
				settingValue, Privacy.PUBLIC.name());

		AccountSetting org = new AccountSetting(null, userId, "organization",
				"organization", Privacy.PUBLIC.name());

		AccountSetting role = new AccountSetting(null, userId, "role", "role",
				Privacy.PUBLIC.name());

		List<AccountSetting> settings = Arrays.asList(setting, org, role);

		User user = Mockito.mock(User.class);

		when(user.getUserId()).thenReturn(userId);

		List<AccountSetting> savedAccountSettings = accountSettingsService
				.setAccountSettings(user, settings);

		savedAccountSettings.forEach(System.out::println);

		assertNotNull(savedAccountSettings);
		assertTrue(savedAccountSettings.contains(setting));
		assertFalse(savedAccountSettings.contains(org));
		assertFalse(savedAccountSettings.contains(role));

	}
}
