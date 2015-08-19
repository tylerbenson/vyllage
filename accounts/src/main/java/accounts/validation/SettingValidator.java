package accounts.validation;

import org.springframework.util.Assert;

import accounts.model.account.settings.AccountSetting;

public abstract class SettingValidator {

	public abstract AccountSetting validate(AccountSetting setting);

	/**
	 * Sets the error message or appends if the setting already has one.
	 * 
	 * @param setting
	 * @param message
	 */
	protected void setErrorMessage(AccountSetting setting, String message) {
		Assert.isTrue(message != null, "Error Message cannot be null");
		if (setting.getErrorMessage() != null)
			setting.setErrorMessage(setting.getErrorMessage() + ", " + message);
		else
			setting.setErrorMessage(message);
	}
}
