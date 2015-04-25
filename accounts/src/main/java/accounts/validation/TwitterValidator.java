package accounts.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import accounts.model.account.settings.AccountSetting;

public class TwitterValidator extends SettingValidator {
	private final String message = "Not a valid Twitter name.";

	private String alphanumericWithDotRegex = "^[a-zA-Z0-9_]{1,15}$";
	final Pattern pattern;

	private Matcher matcher;

	public TwitterValidator() {
		pattern = Pattern.compile(alphanumericWithDotRegex);
	}

	@Override
	public AccountSetting validate(AccountSetting setting) {

		if (setting.getValue() == null)
			return setting;

		matcher = pattern.matcher(setting.getValue());

		if (!matcher.matches())
			setErrorMessage(setting, message);

		return setting;
	}
}
