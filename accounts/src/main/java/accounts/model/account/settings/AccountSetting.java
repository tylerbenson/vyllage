package accounts.model.account.settings;

import lombok.NonNull;
import lombok.ToString;
import user.common.constants.AccountSettingsEnum;

@ToString
public class AccountSetting {

	private Long accountSettingId;
	private Long userId;
	private String name;
	private String value;
	private String privacy;
	private String errorMessage;

	public AccountSetting() {
	}

	public AccountSetting(Long accountSettingId, Long userId, String name,
			String value, String privacy) {
		super();
		this.accountSettingId = accountSettingId;
		this.userId = userId;
		this.name = name;
		this.value = value;
		this.privacy = privacy;
	}

	public Long getAccountSettingId() {
		return accountSettingId;
	}

	public void setAccountSettingId(Long accountSettingId) {
		this.accountSettingId = accountSettingId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static AccountSetting createEmailUpdatesSetting(@NonNull final Long userId) {
		AccountSetting emailUpdatesSetting = new AccountSetting();
		emailUpdatesSetting.setName(AccountSettingsEnum.emailUpdates.name());
		emailUpdatesSetting.setUserId(userId);
		emailUpdatesSetting.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
		emailUpdatesSetting.setValue(EmailFrequencyUpdates.NEVER.name()
				.toLowerCase());
		return emailUpdatesSetting;
	}

	public static AccountSetting createAvatarSetting(@NonNull final Long userId) {
		AccountSetting avatarSetting = new AccountSetting();
		avatarSetting.setName(AccountSettingsEnum.avatar.name());
		avatarSetting.setUserId(userId);
		avatarSetting.setPrivacy(Privacy.PUBLIC.name().toLowerCase());
		avatarSetting.setValue(AvatarSourceEnum.GRAVATAR.name().toLowerCase());
		return avatarSetting;
	}

}
