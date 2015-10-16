package accounts.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import accounts.model.account.settings.AccountSetting;

public class FacebookValidator extends SettingValidator {
	private final String message = "Not a valid Facebook name.";

	private final Pattern pattern;

	public FacebookValidator() {
		String alphanumericWithDotRegex = "^[a-zA-Z0-9\\.]*$";

		pattern = Pattern.compile(alphanumericWithDotRegex);
	}

	@Override
	public AccountSetting validate(AccountSetting setting) {

		if (setting.getValue() == null || setting.getValue().isEmpty())
			return setting;

		Matcher matcher = pattern.matcher(setting.getValue());

		if (!matcher.matches())
			setErrorMessage(setting, message);

		return setting;
	}
}
