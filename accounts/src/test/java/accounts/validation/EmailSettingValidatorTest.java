package accounts.validation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import user.common.User;
import accounts.model.account.settings.AccountSetting;
import accounts.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class EmailSettingValidatorTest {

	@Mock
	private UserService userService;

	private User user;

	@Before
	public void setUp() {
		when(userService.userExists(Mockito.anyString())).thenReturn(false);

		user = this.generateAndLoginUser();
		when(user.getUsername()).thenReturn("test1@gmail.com");
	}

	@Test
	public void testEmailSettingValidatorReturnsOk() {

		EmailSettingValidator validator = new EmailSettingValidator(userService);
		AccountSetting as = new AccountSetting();
		// changes from test1@gmail.com to test2@gmail.com
		as.setValue("test2@gmail.com");

		AccountSetting setting = validator.validate(as);

		assertTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForInvalidEmail() {
		EmailSettingValidator validator = new EmailSettingValidator(userService);
		AccountSetting as = new AccountSetting();
		as.setValue("test");

		AccountSetting setting = validator.validate(as);

		assertTrue(EmailSettingValidator.INVALID_EMAIL_ADDRESS_MESSAGE
				.equals(setting.getErrorMessage()));
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForNullEmail() {
		EmailSettingValidator validator = new EmailSettingValidator(userService);
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		assertTrue(EmailSettingValidator.INVALID_EMAIL_ADDRESS_MESSAGE
				.equals(setting.getErrorMessage()));
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForEmptyValue() {
		EmailSettingValidator validator = new EmailSettingValidator(userService);
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		assertTrue(EmailSettingValidator.INVALID_EMAIL_ADDRESS_MESSAGE
				.equals(setting.getErrorMessage()));
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForExistingEmail() {
		EmailSettingValidator validator = new EmailSettingValidator(userService);
		AccountSetting as = new AccountSetting();
		// changes from test1@gmail.com to user@vyllage.com
		as.setValue("user@vyllage.com");

		when(userService.userExists(Mockito.anyString())).thenReturn(true);

		AccountSetting setting = validator.validate(as);

		assertTrue(EmailSettingValidator.EMAIL_ALREADY_TAKEN_MESSAGE
				.equals(setting.getErrorMessage()));
	}

	private User generateAndLoginUser() {
		User o = Mockito.mock(User.class);

		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(o);

		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);
		return o;
	}
}
