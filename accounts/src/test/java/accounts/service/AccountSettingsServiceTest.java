package accounts.service;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import accounts.Application;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.ElementNotFoundException;
import accounts.repository.UserNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccountSettingsServiceTest {

	@Inject
	private AccountSettingsService accountSettingsService;

	@Autowired
	private UserService userService;

	@Mock
	private User user;

	@Test
	public void saveAccountSetting() throws ElementNotFoundException {
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

		List<AccountSetting> settings = accountSettingsService
				.getAccountSetting(u, emailUpdates);
		Assert.assertEquals(emailUpdates, settings.get(0).getName());
		Assert.assertEquals(value, settings.get(0).getValue());
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

		Mockito.when(u.getUserId()).thenReturn(userId);

		accountSettingsService.setAccountSetting(u, as);

		List<AccountSetting> settings = accountSettingsService
				.getAccountSetting(u, fieldName);
		Assert.assertEquals(fieldName, settings.get(0).getName());
		Assert.assertEquals(value, settings.get(0).getValue());

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
}
