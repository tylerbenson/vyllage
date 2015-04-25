package accounts.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import accounts.model.account.settings.AccountSetting;

public class OnlyAlphanumericValidator extends SettingValidator {

	private final String message = "Only alphanumeric characters are allowed.";

	private String alphanumericRegex = "^[a-zA-Z0-9]*$";

	final Pattern pattern;

	private Matcher matcher;

	public OnlyAlphanumericValidator() {
		pattern = Pattern.compile(alphanumericRegex);
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
