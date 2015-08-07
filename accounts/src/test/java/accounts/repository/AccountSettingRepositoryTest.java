package accounts.repository;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccountSettingRepositoryTest {

	@Autowired
	private AccountSettingRepository accountSettingRepository;

	@Test
	public void saveTest() {
		Long userId = 0L;
		AccountSetting setting = new AccountSetting();
		String test = "test";
		String aValue = "aValue";

		setting.setName(test);
		setting.setValue(aValue);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);

		Optional<AccountSetting> savedSetting = accountSettingRepository.get(
				userId, test);

		assertTrue(savedSetting.isPresent());
		assertTrue(savedSetting.get().getAccountSettingId() != null
				&& savedSetting.get().getName().equalsIgnoreCase(test)
				&& savedSetting.get().getErrorMessage() == null
				&& savedSetting.get().getPrivacy() != null
				&& savedSetting.get().getValue().equalsIgnoreCase(aValue));
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void saveTestWrongUserId() {
		Long userId = -2L;
		AccountSetting setting = new AccountSetting();
		String test = "test";
		String aValue = "aValue";

		setting.setName(test);
		setting.setValue(aValue);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void saveNullValue() {
		Long userId = 0L;
		AccountSetting setting = new AccountSetting();
		String test = "test";

		setting.setName(test);
		setting.setValue(null);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void saveNullPrivacy() {
		Long userId = 0L;
		AccountSetting setting = new AccountSetting();
		String test = "test";
		String aValue = "aValue";

		setting.setName(test);
		setting.setValue(aValue);
		setting.setPrivacy(null);
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void saveNullUserId() {
		Long userId = null;
		AccountSetting setting = new AccountSetting();
		String test = "test";
		String aValue = "aValue";

		setting.setName(test);
		setting.setValue(aValue);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);
	}

	@Test()
	public void testDeleteByName() {
		Long userId = 0L;
		AccountSetting setting = new AccountSetting();
		String test = "delete";
		String aValue = "aValue";

		setting.setName(test);
		setting.setValue(aValue);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);
		accountSettingRepository.deleteByName(userId, test);

		Assert.assertFalse(accountSettingRepository.get(userId, test)
				.isPresent());
	}

	public void testDeleteByUserId() {
		Long userId = 1L;

		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserId()).thenReturn(userId);

		AccountSetting setting = new AccountSetting();
		String test = "delete";
		String aValue = "aValue";

		setting.setName(test);
		setting.setValue(aValue);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting);

		AccountSetting setting2 = new AccountSetting();
		String test2 = "delete";
		String aValue2 = "aValue";

		setting.setName(test2);
		setting.setValue(aValue2);
		setting.setPrivacy(Privacy.PRIVATE.name());
		setting.setUserId(userId);

		accountSettingRepository.set(userId, setting2);

		accountSettingRepository.deleteByUserId(userId);

		accountSettingRepository.getAccountSettings(user);
		assertTrue(accountSettingRepository.getAccountSettings(user).isEmpty());
	}

}
