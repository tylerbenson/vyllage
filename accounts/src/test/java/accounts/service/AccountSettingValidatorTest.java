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
	public void testEmailSettingValidatorReturnsOk() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("test@gmail.com");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForInvalidEmail() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("test");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForNullEmail() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testEmailSettingValidatorReturnsWithErrorForEmptyValue() {
		EmailSettingValidator validator = new EmailSettingValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testLengthSettingValidatorReturnsOk() {
		int length = 5;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("12345");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testLengthSettingValidatorReturnsWithError() {
		int length = 30;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testLengthSettingValidatorReturnsWithErrorAndMessageEquals() {
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
	public void testLengthSettingValidatorNullOk() {
		int length = 5;
		LengthValidator validator = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void testNotNullSettingValidatorReturnsOk() {
		NotNullValidator validator = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testNotNullSettingValidatorReturnsWithError() {
		NotNullValidator validator = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testNotNullPhoneNumberSettingValidator() {
		PhoneNumberValidator validator1 = new PhoneNumberValidator();
		NotNullValidator validator2 = new NotNullValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator2.validate(validator1.validate(as));
		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testPhoneNumberSettingValidatorReturnsOk() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("1234567890");

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testPhoneNumberSettingValidatorReturnsWithError() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("a234567890");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testPhoneNumberSettingValidatorReturnsWithLengthError1() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("12");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testPhoneNumberSettingValidatorReturnsWithLengthError2() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("1234567890505056406540");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testPhoneNumberSettingValidatorBlankValuesOk() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("     ");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void testPhoneNumberSettingValidatorEmptyValuesOk() {
		PhoneNumberValidator validator = new PhoneNumberValidator();
		AccountSetting as = new AccountSetting();
		as.setValue("");

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void testPhoneNumberValidatorAndLengthSettingValidatorReturnsWithErrorAndMessageContainsComma() {
		int length = 5;
		PhoneNumberValidator validator1 = new PhoneNumberValidator();
		LengthValidator validator2 = new LengthValidator(length);
		AccountSetting as = new AccountSetting();
		as.setValue("123456");

		AccountSetting setting = validator2.validate(validator1.validate(as));

		Assert.isTrue(setting.getErrorMessage() != null);
		Assert.isTrue(setting.getErrorMessage().contains(","));
	}

	@Test
	public void testURLValidatorEmptyOk() {
		final String url = "";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(url);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void testURLValidatorNullOk() {
		final String url = null;

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(url);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() == null);
	}

	@Test
	public void testURLValidatorNotUrl() {
		final String url = "https:.ae///";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(url);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testURLValidatorFacebookTest() {
		final String facebook = "https://www.facebook.com/my.name";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(facebook);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testURLValidatorLinkedInTest() {
		final String linkedIn = "https://ar.linkedin.com/in/my.name";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(linkedIn);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testURLValidatorTwitterTest() {
		final String twitter = "https://twitter.com/my.name";

		URLValidator validator = new URLValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(twitter);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testOnlyAlphanumericSuccess() {
		final String value = "aeiou123456";

		OnlyAlphanumericValidator validator = new OnlyAlphanumericValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testOnlyAlphanumericNullOk() {

		OnlyAlphanumericValidator validator = new OnlyAlphanumericValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(null);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testOnlyAlphanumericFail() {
		final String value = "/*+55aer";

		OnlyAlphanumericValidator validator = new OnlyAlphanumericValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testFaceBookSuccess() {
		final String value = "aeiou.123456";

		FacebookValidator validator = new FacebookValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testFaceBookNullAllowed() {
		final String value = null;

		FacebookValidator validator = new FacebookValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testFaceBookEmptyAllowed() {
		final String value = "";

		FacebookValidator validator = new FacebookValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testFaceBookFail() {
		final String value = "/*+5.";

		FacebookValidator validator = new FacebookValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testTwitterSuccess() {
		final String value = "aeiou___123456";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testTwitterNull() {
		final String value = null;

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testTwitterEmpty() {
		final String value = "";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isNull(setting.getErrorMessage());
	}

	@Test
	public void testTwitterFail() {
		final String value = "/*+5.";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

	@Test
	public void testTwitterLengthFail() {
		final String value = "a123456789101112131415";

		TwitterValidator validator = new TwitterValidator();
		AccountSetting as = new AccountSetting();
		as.setValue(value);

		AccountSetting setting = validator.validate(as);

		Assert.isTrue(setting.getErrorMessage() != null);
	}

}
