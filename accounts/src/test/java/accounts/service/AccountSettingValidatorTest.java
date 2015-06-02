package accounts.service;

import org.junit.Test;
import org.springframework.util.Assert;

import accounts.model.account.settings.AccountSetting;
import accounts.validation.EmailSettingValidator;
import accounts.validation.FacebookValidator;
import accounts.validation.LengthValidator;
import accounts.validation.NotNullValidator;
import accounts.validation.OnlyAlphanumericValidator;
import accounts.validation.PhoneNumberValidator;
import accounts.validation.TwitterValidator;
import accounts.validation.URLValidator;

public class AccountSettingValidatorTest {

	@Test
	public void EmailSettingValidatorReturnsOk() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("test@gmail.com");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void EmailSettingValidatorReturnsWithErrorForInvalidEmail() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("test");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void EmailSettingValidatorReturnsWithErrorForNullEmail() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void EmailSettingValidatorReturnsWithErrorForEmptyValue() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void LengthSettingValidatorReturnsOk() {
		int length = 5;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("12345");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void LengthSettingValidatorReturnsWithError() {
		int length = 30;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void LengthSettingValidatorReturnsWithErrorAndMessageEquals() {
		int length = 5;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("123456");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
		Assert.isTrue(setting.getErrorMessage().equalsIgnoreCase(
				"Up to " + length + " characters allowed."));
	}

	@Test
	public void NotNullSettingValidatorReturnsOk() {
		NotNullValidator validator = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void NotNullSettingValidatorReturnsWithError() {
		NotNullValidator validator = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void NumberSettingValidatorReturnsOk() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("1234567890");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void NumberSettingValidatorReturnsWithError() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("a");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void NumberSettingValidatorReturnsWithLengthError1() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("12");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void NumberSettingValidatorReturnsWithLengthError2() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("1234567890505056406540");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void URLValidatorFacebookTest() {
		final String facebook = "https://www.facebook.com/my.name";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(facebook);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void URLValidatorLinkedInTest() {
		final String linkedIn = "https://ar.linkedin.com/in/my.name";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(linkedIn);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void URLValidatorTwitterTest() {
		final String twitter = "https://twitter.com/my.name";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(twitter);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void onlyAlphanumericSuccess() {
		final String value = "aeiou123456";

		OnlyAlphanumericValidator validator = new OnlyAlphanumericValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void onlyAlphanumericFail() {
		final String value = "/*+55aer";

		OnlyAlphanumericValidator validator = new OnlyAlphanumericValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void faceBookSuccess() {
		final String value = "aeiou.123456";

		FacebookValidator validator = new FacebookValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void faceBookFail() {
		final String value = "/*+5.";

		FacebookValidator validator = new FacebookValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void twitterSuccess() {
		final String value = "aeiou___123456";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void twitterFail() {
		final String value = "/*+5.";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void twitterLengthFail() {
		final String value = "a123456789101112131415";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

}
