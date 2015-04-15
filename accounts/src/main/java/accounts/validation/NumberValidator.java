package accounts.validation;

import accounts.model.account.settings.AccountSetting;

public class NumberValidator extends SettingValidator {

	public final String errorMessage = "Only numbers are allowed.";

	@Override
	public AccountSetting validate(AccountSetting setting) {
		try {
			Long.parseLong(setting.getValue());
		} catch (NumberFormatException e) {
			setErrorMessage(setting, errorMessage);
		}

		return setting;
	}
}
