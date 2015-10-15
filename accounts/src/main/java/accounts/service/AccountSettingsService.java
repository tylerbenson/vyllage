package accounts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.User;
import user.common.constants.AccountSettingsEnum;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.service.aspects.CheckPrivacy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.NewRelic;

@Service
public class AccountSettingsService {

	private final Logger logger = Logger.getLogger(AccountSettingsService.class
			.getName());

	private final UserService userService;

	private final AccountSettingRepository accountSettingRepository;

	@Inject
	public AccountSettingsService(final UserService userService,
			final AccountSettingRepository accountSettingRepository) {
		super();
		this.userService = userService;
		this.accountSettingRepository = accountSettingRepository;
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSettings(List<Long> userIds) {
		return appendUserNames(accountSettingRepository
				.getAccountSettings(userIds));
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSettings(User user) {
		return appendUserNames(accountSettingRepository
				.getAccountSettings(user));
	}

	@CheckPrivacy
	public Optional<AccountSetting> getAccountSetting(@NonNull final User user,
			@NonNull final String settingName) {

		switch (settingName) {
		case "firstName":
			return Optional.of(new AccountSetting(null, user.getUserId(),
					AccountSettingsEnum.firstName.name(), user.getFirstName(),
					Privacy.PUBLIC.name()));

		case "middleName":
			return Optional.of(new AccountSetting(null, user.getUserId(),
					AccountSettingsEnum.middleName.name(),
					user.getMiddleName(), Privacy.PUBLIC.name()));

		case "lastName":
			return Optional.of(new AccountSetting(null, user.getUserId(),
					AccountSettingsEnum.lastName.name(), user.getLastName(),
					Privacy.PUBLIC.name()));

		case "email":
			return Optional.of(new AccountSetting(null, user.getUserId(),
					AccountSettingsEnum.email.name(), user.getUsername(),
					Privacy.PUBLIC.name()));

		default:
			return accountSettingRepository.get(user.getUserId(), settingName);
		}
	}

	protected List<AccountSetting> appendUserNames(
			List<AccountSetting> accountSettings) {

		accountSettings.addAll(getUserNamesAndEmail(accountSettings.stream()
				.map(as -> as.getUserId()).collect(Collectors.toList())));

		return accountSettings;
	}

	protected List<AccountSetting> getUserNamesAndEmail(List<Long> userIds) {
		List<AccountSetting> settings = new ArrayList<>();

		for (User accountNames : userService.getUsers(userIds)) {
			settings.add(new AccountSetting(null, accountNames.getUserId(),
					AccountSettingsEnum.firstName.name(), accountNames
							.getFirstName(), Privacy.PUBLIC.name()));

			settings.add(new AccountSetting(null, accountNames.getUserId(),
					AccountSettingsEnum.middleName.name(), accountNames
							.getMiddleName(), Privacy.PUBLIC.name()));

			settings.add(new AccountSetting(null, accountNames.getUserId(),
					AccountSettingsEnum.lastName.name(), accountNames
							.getLastName(), Privacy.PUBLIC.name()));

			settings.add(new AccountSetting(null, accountNames.getUserId(),
					AccountSettingsEnum.email.name(), accountNames
							.getUsername(), Privacy.PUBLIC.name()));
		}

		return settings;
	}

	public AccountSetting setAccountSetting(final User user,
			final AccountSetting setting) {
		Assert.notNull(setting);

		if (setting.getUserId() == null)
			setting.setUserId(user.getUserId());

		switch (setting.getName()) {
		case "firstName":
			return setFirstName(user, setting);

		case "middleName":
			return setMiddleName(user, setting);

		case "lastName":
			return setLastName(user, setting);

		case "email":
			// don't change email if they are the same or the email already
			// exists.
			if (!user.getUsername().equalsIgnoreCase(setting.getValue())
					&& !userService.userExists(setting.getValue())) {
				try {

					userService.sendEmailChangeConfirmation(user,
							setting.getValue());
				} catch (EmailException | JsonProcessingException e) {
					logger.severe(ExceptionUtils.getStackTrace(e));
					NewRelic.noticeError(e);
				}

				// save the new email as a new setting to query from the
				// frontend.
				AccountSetting newEmailSetting = new AccountSetting(null,
						setting.getUserId(),
						AccountSettingsEnum.newEmail.name(),
						setting.getValue(), setting.getPrivacy());

				accountSettingRepository.set(newEmailSetting);

				// but don't change the setting value yet, the user needs to
				// confirm the change by mail.
				setting.setValue(user.getUsername());
			}

			return setting;
		default:
			return accountSettingRepository.set(setting);
		}
	}

	public List<AccountSetting> setAccountSettings(final User user,
			List<AccountSetting> settings) {

		List<AccountSetting> savedSettings = new ArrayList<>();

		savedSettings.addAll(settings
				.stream()
				.filter(set -> !set.getName().equalsIgnoreCase("role")
						&& !set.getName().equalsIgnoreCase("organization"))
				.map(set -> this.setAccountSetting(user, set))
				.collect(Collectors.toList()));

		return savedSettings;
	}

	/**
	 * Updates the user's name.
	 *
	 * @param user
	 * @param setting
	 */
	protected AccountSetting setFirstName(User user, AccountSetting setting) {

		user.setFirstName(setting.getValue());
		userService.update(user);

		return setting;
	}

	/**
	 * Updates the user's name.
	 *
	 * @param user
	 * @param setting
	 */
	protected AccountSetting setMiddleName(User user, AccountSetting setting) {

		user.setMiddleName(setting.getValue());
		userService.update(user);

		return setting;
	}

	/**
	 * Updates the user's name.
	 *
	 * @param user
	 * @param setting
	 */
	protected AccountSetting setLastName(User user, AccountSetting setting) {

		user.setLastName(setting.getValue());
		userService.update(user);

		return setting;
	}

}
