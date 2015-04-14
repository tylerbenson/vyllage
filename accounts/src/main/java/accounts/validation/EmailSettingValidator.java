package accounts.validation;

import org.springframework.beans.factory.annotation.Value;

import accounts.model.account.settings.AccountSetting;

public class EmailSettingValidator extends SettingValidator {

	@Value("${error.invalid.email:Invalid email address.}")
	public final String errorMessage = null;

	@Override
	public AccountSetting validate(AccountSetting setting) {

		if (setting.getValue() == null || setting.getValue().isEmpty()
				|| EmailValidator.validate(setting.getValue()))
			setErrorMessage(setting, errorMessage);

		return null;
	}

}
