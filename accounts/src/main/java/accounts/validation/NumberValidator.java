package accounts.validation;

import org.springframework.beans.factory.annotation.Value;

import accounts.model.account.settings.AccountSetting;

public class NumberValidator extends SettingValidator {

	@Value("${error.must.be.number:Only numbers are allowed.}")
	public final String errorMessage = null;

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
