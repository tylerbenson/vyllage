package accounts.service.aspects;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import accounts.Application;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.service.AccountSettingsService;
import accounts.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CheckPrivacyAspectTest {

	private UserService userService = Mockito.mock(UserService.class);
	private AccountSettingRepository accountSettingRepository = Mockito
			.mock(AccountSettingRepository.class);

	private final AccountSettingsService service = new AccountSettingsService(
			userService, accountSettingRepository);

	@Test(expected = NullPointerException.class)
	public void testCheckPrivacyNullUser() {
		service.getAccountSetting(null, "a");
	}

	@Test(expected = NullPointerException.class)
	public void testCheckPrivacyNullSettingName() {
		User user = Mockito.mock(User.class);

		service.getAccountSetting(user, null);
	}

	@Test
	public void testCheckPrivacyReturnOwnSettings() {
		Long userId = 0L;
		String settingName = "email";

		User user = generateAndLoginUser();
		Mockito.when(user.getUserId()).thenReturn(userId);

		AccountSetting as = new AccountSetting(null, userId, settingName,
				"aaa@bbb.com", Privacy.PUBLIC.name());

		Mockito.when(accountSettingRepository.getAccountSettings(user))
				.thenReturn(Arrays.asList(as));

		List<AccountSetting> accountSettings = service.getAccountSettings(user);

		Assert.assertNotNull(accountSettings);
		Assert.assertFalse(accountSettings.isEmpty());

	}

	private User generateAndLoginUser() {
		User o = Mockito.mock(User.class);

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(
				authentication);
		SecurityContextHolder.setContext(securityContext);
		return o;
	}
}
