package accounts.validation;

import accounts.model.account.settings.AccountSetting;

/**
 * Checks that a given setting does not have a null value.
 * 
 * @author uh
 *
 */
public class NotNullValidator extends SettingValidator {

	public final String errorMessage = "Field is required.";

	@Override
	public AccountSetting validate(AccountSetting setting) {
		if (setting.getValue() == null)
			setErrorMessage(setting, errorMessage);

		return setting;
	}

}
