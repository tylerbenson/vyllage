package accounts.validation;

import accounts.model.account.settings.AccountSetting;

public abstract class SettingValidator {

	public abstract AccountSetting validate(AccountSetting setting);

	protected void setErrorMessage(AccountSetting setting, String message) {
		assert message != null;
		if (setting.getErrorMessage() != null)
			setting.setErrorMessage(setting.getErrorMessage() + ", " + message);
		else
			setting.setErrorMessage(message);
	}
}
