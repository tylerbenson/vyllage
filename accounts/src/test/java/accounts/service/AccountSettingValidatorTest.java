package accounts.service;

import org.junit.Test;
import org.springframework.util.Assert;

import accounts.model.account.settings.AccountSetting;
import accounts.validation.EmailSettingValidator;
import accounts.validation.LengthValidator;
import accounts.validation.NotNullValidator;
import accounts.validation.NumberValidator;

public class AccountSettingValidatorTest {

	@Test
	public void EmailSettingValidatorReturnsOk() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("test@gmail.com");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void EmailSettingValidatorReturnsWithErrorForInvalidEmail() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("test");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
	}

	@Test
	public void EmailSettingValidatorReturnsWithErrorForNullEmail() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
	}

	@Test
	public void EmailSettingValidatorReturnsWithErrorForEmptyValue() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
	}

	@Test
	public void LengthSettingValidatorReturnsOk() {
		int length = 5;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("12345");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void LengthSettingValidatorReturnsWithError() {
		int length = 30;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
	}

	@Test
	public void LengthSettingValidatorReturnsWithErrorAndMessageEquals() {
		int length = 5;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("123456");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
		Assert.isTrue(setting.getErrorMessage().equalsIgnoreCase(
				"Up to " + length + " characters allowed."));
	}

	@Test
	public void NotNullSettingValidatorReturnsOk() {
		NotNullValidator validator = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void NotNullSettingValidatorReturnsWithError() {
		NotNullValidator validator = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
	}

	@Test
	public void NumberSettingValidatorReturnsOk() {
		NumberValidator validator = new NumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("50");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void NumberSettingValidatorReturnsWithError() {
		NumberValidator validator = new NumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("a");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(!setting.getErrorMessage().isEmpty());
	}

}
