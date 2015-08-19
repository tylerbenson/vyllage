package accounts.validation;

import java.text.MessageFormat;

import accounts.model.account.settings.AccountSetting;

public class LengthValidator extends SettingValidator {

	public final String errorMessage = "Up to {0} characters allowed.";

	public final int length;

	public LengthValidator(int length) {
		this.length = length;
	}

	@Override
	public AccountSetting validate(AccountSetting setting) {
		if (setting.getValue() != null && setting.getValue().length() > length)
			setErrorMessage(setting, MessageFormat.format(errorMessage, length));
		return setting;
	}

}
