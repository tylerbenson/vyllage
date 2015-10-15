package accounts.validation;

import accounts.model.account.settings.AccountSetting;
import accounts.service.UserService;

public class EmailSettingValidator extends SettingValidator {

	public static final String INVALID_EMAIL_ADDRESS_MESSAGE = "Invalid email address.";
	public static final String EMAIL_ALREADY_TAKEN_MESSAGE = "Email address already in use.";

	private final UserService userService;

	public EmailSettingValidator(final UserService userService) {
		this.userService = userService;
	}

	@Override
	public AccountSetting validate(AccountSetting setting) {

		if (setting.getValue() == null || setting.getValue().isEmpty()
				|| !EmailValidator.isValid(setting.getValue()))
			setErrorMessage(setting, INVALID_EMAIL_ADDRESS_MESSAGE);

		if (userService.userExists(setting.getValue()))
			setErrorMessage(setting, EMAIL_ALREADY_TAKEN_MESSAGE);

		return setting;
	}

}
