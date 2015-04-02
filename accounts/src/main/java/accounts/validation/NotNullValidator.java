package accounts.validation;

import org.springframework.beans.factory.annotation.Value;

import accounts.model.account.settings.AccountSetting;

public class NotNullValidator extends SettingValidator {

	@Value("${error.length:Field is required.}")
	public final String errorMessage = null;

	@Override
	public AccountSetting validate(AccountSetting setting) {
		if (setting.getValue() == null)
			setErrorMessage(setting, errorMessage);

		return setting;
	}

}
