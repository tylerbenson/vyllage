package accounts.validation;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Value;

import accounts.model.account.settings.AccountSetting;

public class LengthValidator extends SettingValidator {

	@Value("${error.length:Up to {0} characters allowed.}")
	public final String errorMessage = null;

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
