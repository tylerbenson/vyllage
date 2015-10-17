package accounts.validation;

import org.springframework.security.core.context.SecurityContextHolder;

import user.common.User;
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

		/**
		 * Only validate when the email changes, this is necessary because the
		 * frontend saves all the values when there's a setting change.
		 */
		boolean emailHasChanged = !getUser().getUsername().equals(
				setting.getValue());

		if (emailHasChanged && userService.userExists(setting.getValue()))
			setErrorMessage(setting, EMAIL_ALREADY_TAKEN_MESSAGE);

		return setting;
	}

	protected User getUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}

}
