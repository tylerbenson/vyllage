package accounts.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.User;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.repository.ElementNotFoundException;
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
	public List<AccountSetting> getAccountSetting(final User user,
			final String settingName) throws ElementNotFoundException {
		Assert.notNull(settingName);

		switch (settingName) {
		case "firstName":
			return Arrays.asList(new AccountSetting(null, user.getUserId(),
					"firstName", user.getFirstName(), Privacy.PUBLIC.name()));

		case "middleName":
			return Arrays.asList(new AccountSetting(null, user.getUserId(),
					"middleName", user.getMiddleName(), Privacy.PUBLIC.name()));

		case "lastName":
			return Arrays.asList(new AccountSetting(null, user.getUserId(),
					"lastName", user.getLastName(), Privacy.PUBLIC.name()));

		default:
			return accountSettingRepository.get(user.getUserId(), settingName);
		}
	}

	protected List<AccountSetting> appendUserNames(
			List<AccountSetting> accountSettings) {

		accountSettings.addAll(getUserNames(accountSettings.stream()
				.map(as -> as.getUserId()).collect(Collectors.toList())));

		return accountSettings;
	}

	protected List<AccountSetting> getUserNames(List<Long> userIds) {
		List<AccountSetting> settings = new ArrayList<>();

		for (AccountNames accountNames : userService.getNames(userIds)) {
			settings.add(new AccountSetting(null, accountNames.getUserId(),
					"firstName", accountNames.getFirstName(), Privacy.PUBLIC
							.name()));

			settings.add(new AccountSetting(null, accountNames.getUserId(),
					"middleName", accountNames.getMiddleName(), Privacy.PUBLIC
							.name()));

			settings.add(new AccountSetting(null, accountNames.getUserId(),
					"lastName", accountNames.getLastName(), Privacy.PUBLIC
							.name()));
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
			try {
				userService.sendEmailChangeConfirmation(user,
						setting.getValue());
			} catch (EmailException | JsonProcessingException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}

			// don't change the setting value yet, the user needs to confirm the
			// change.
			setting.setValue(user.getUsername());
			return accountSettingRepository.set(user.getUserId(), setting);
		default:
			return accountSettingRepository.set(user.getUserId(), setting);
		}
	}

	public List<AccountSetting> setAccountSettings(final User user,
			List<AccountSetting> settings) {

		List<AccountSetting> savedSettings = new ArrayList<>();

		savedSettings.addAll(settings
				.stream()
				.filter(set -> !set.getName().equalsIgnoreCase("role")
						&& !set.getName().equalsIgnoreCase("organization"))
				.map(set -> setAccountSetting(user, set))
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

	// /**
	// * Changes the username and email, they are the same thing.
	// *
	// * @param user
	// * @param setting
	// */
	// protected void setEmail(User user, AccountSetting setting) {
	// if (setting.getValue() != null && !setting.getValue().isEmpty()) {
	// // username is final...
	// // password is erased after the user logins but we need something
	// // here, even if we won't change it
	//
	// List<UserOrganizationRole> uor = new ArrayList<>();
	//
	// for (GrantedAuthority grantedAuthority : user.getAuthorities())
	// uor.add((UserOrganizationRole) grantedAuthority);
	//
	// User newUser = new User(user.getUserId(), user.getFirstName(),
	// user.getMiddleName(), user.getLastName(),
	// setting.getValue(), "a password we don't care about",
	// user.isEnabled(), user.isAccountNonExpired(),
	// user.isCredentialsNonExpired(), user.isAccountNonLocked(),
	// uor, user.getDateCreated(), user.getLastModified());
	// userService.update(newUser);
	//
	// userService.changeEmail(user, setting.getValue());
	//
	// }
	//
	// }
}
