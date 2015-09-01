package accounts.service.aspects;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

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
import accounts.service.AccountSettingsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CheckPrivacyAspectTest {

	// private UserService userService = Mockito.mock(UserService.class);
	// private AccountSettingRepository accountSettingRepository = Mockito
	// .mock(AccountSettingRepository.class);

	@Inject
	private AccountSettingsService service;

	@Test(expected = IllegalArgumentException.class)
	public void testCheckPrivacyNullUser() {
		service.getAccountSetting(null, "a");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCheckPrivacyNullSettingName() {
		User user = Mockito.mock(User.class);

		service.getAccountSetting(user, null);
	}

	@Test
	public void testCheckPrivacyReturnsOk() {
		Long loggedInUserId = 0L;
		User loggedInUser = generateAndLoginUser();

		Mockito.when(loggedInUser.getUserId()).thenReturn(loggedInUserId);

		List<AccountSetting> accountSettings = service
				.getAccountSettings(Arrays.asList(0L));

		Assert.assertNotNull(accountSettings);
		Assert.assertFalse(accountSettings.isEmpty());
		Assert.assertTrue(accountSettings.size() >= 7);

	}

	@Test
	public void testCheckPrivacyReturnsEmpty() {
		Long loggedInUserId = 16L;

		String settingName = "settingName";

		User loggedInUser = generateAndLoginUser();

		Mockito.when(loggedInUser.getUserId()).thenReturn(loggedInUserId);

		List<AccountSetting> accountSettings = service
				.getAccountSettings(Arrays.asList(0L));

		Assert.assertNotNull(accountSettings);
		Assert.assertTrue(accountSettings.stream().noneMatch(
				as -> settingName.equalsIgnoreCase(as.getName())));

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
