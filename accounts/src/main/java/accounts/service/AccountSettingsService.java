package accounts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import user.common.User;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.AccountSettingRepository;
import accounts.repository.ElementNotFoundException;
import accounts.service.aspects.CheckPrivacy;

@Service
public class AccountSettingsService {

	private UserService userService;

	private AccountSettingRepository accountSettingRepository;

	@Inject
	public AccountSettingsService(UserService userService,
			AccountSettingRepository accountSettingRepository) {
		super();
		this.userService = userService;
		this.accountSettingRepository = accountSettingRepository;
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSettings(List<Long> userIds) {
		return accountSettingRepository.getAccountSettings(userIds);
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSettings(User user) {
		return accountSettingRepository.getAccountSettings(user);
	}

	@CheckPrivacy
	public List<AccountSetting> getAccountSetting(final User user,
			final String settingName) throws ElementNotFoundException {
		assert settingName != null;

		return accountSettingRepository.get(user.getUserId(), settingName);
	}

	public AccountSetting setAccountSetting(final User user,
			AccountSetting setting) {

		if (setting.getUserId() == null)
			setting.setUserId(user.getUserId());

		switch (setting.getName()) {
		case "firstName":
			setFirstName(user, setting);
			return accountSettingRepository.set(user.getUserId(), setting);

		case "middleName":
			setMiddleName(user, setting);
			return accountSettingRepository.set(user.getUserId(), setting);

		case "lastName":
			setLastName(user, setting);
			return accountSettingRepository.set(user.getUserId(), setting);

		case "email":
			setEmail(user, setting);
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
						|| !set.getName().equalsIgnoreCase("organization"))
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
	protected void setFirstName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setFirstName(setting.getValue());
			userService.update(user);
		}
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setMiddleName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setMiddleName(setting.getValue());
			userService.update(user);
		}
	}

	/**
	 * Updates the user's name.
	 * 
	 * @param user
	 * @param setting
	 */
	protected void setLastName(User user, AccountSetting setting) {

		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			user.setLastName(setting.getValue());
			userService.update(user);
		}
	}

	// changes the username and email...
	protected void setEmail(User user, AccountSetting setting) {
		if (setting.getValue() != null && !setting.getValue().isEmpty()) {
			// username is final...
			// password is erased after the user logins but we need something
			// here, even if we won't change it
			User newUser = new User(user.getUserId(), user.getFirstName(),
					user.getMiddleName(), user.getLastName(),
					setting.getValue(), "a password we don't care about",
					user.isEnabled(), user.isAccountNonExpired(),
					user.isCredentialsNonExpired(), user.isAccountNonLocked(),
					user.getAuthorities(), user.getDateCreated(),
					user.getLastModified());
			userService.update(newUser);
		}

	}

}
