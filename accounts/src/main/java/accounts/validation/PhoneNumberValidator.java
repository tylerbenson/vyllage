package accounts.validation;

import org.apache.commons.lang3.StringUtils;

import accounts.model.account.settings.AccountSetting;

/**
 * Validates a phone number, must be 10 digits long or blank.
 * 
 * @author uh
 *
 */
public class PhoneNumberValidator extends SettingValidator {

	public final String errorMessage = "Only numbers are allowed.";

	public final String errorMessageLength = "Phone number must be 10 digits long.";

	@Override
	public AccountSetting validate(AccountSetting setting) {

		// blank values are allowed.
		if (setting.getValue() == null
				|| StringUtils.isBlank(setting.getValue()))
			return setting;
		else if (setting.getValue().length() < 10
				|| setting.getValue().length() >= 11) {
			setErrorMessage(setting, errorMessageLength);
			return setting;
		}

		try {
			Long.parseLong(setting.getValue());
		} catch (NumberFormatException e) {
			setErrorMessage(setting, errorMessage);
		}

		return setting;
	}
}
