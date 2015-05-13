package accounts.validation;

import accounts.model.account.settings.AccountSetting;

public class EmailSettingValidator extends SettingValidator {

	public final String errorMessage = "Invalid email address.";

	@Override
	public AccountSetting validate(AccountSetting setting) {

		if (setting.getValue() == null || setting.getValue().isEmpty()
				|| !EmailValidator.isValid(setting.getValue()))
			setErrorMessage(setting, errorMessage);

		return setting;
	}

}
