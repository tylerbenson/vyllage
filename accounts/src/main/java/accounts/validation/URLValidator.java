package accounts.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import accounts.model.account.settings.AccountSetting;

public class URLValidator extends SettingValidator {

	// http://www.leveluplunch.com/java/examples/validate-url/ :)
	final String urlExpression = "\\b(https?)://"
			+ "[-A-Za-z0-9+&@#/%?=~_|!:,.;]" + "*[-A-Za-z0-9+&@#/%=~_|]";

	final Pattern pattern;

	private Matcher matcher;

	private String errorMessage = "Not a valid URL.";

	public URLValidator() {
		pattern = Pattern.compile(urlExpression);
	}

	@Override
	public AccountSetting validate(AccountSetting setting) {

		if (setting.getValue() == null || setting.getValue().isEmpty())
			return setting;

		matcher = pattern.matcher(setting.getValue());

		if (!matcher.matches())
			setErrorMessage(setting, errorMessage);

		return setting;
	}

}
