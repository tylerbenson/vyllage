package accounts.model.account.settings;

import java.util.Arrays;
import java.util.List;

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

	public AccountSetting(Long accountSettingId, @NonNull Long userId,
			@NonNull String name, String value, @NonNull String privacy) {
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

	public static AccountSetting createEmailUpdatesSetting(
			@NonNull final Long userId) {

		AccountSetting emailUpdatesSetting = new AccountSetting();

		emailUpdatesSetting.setName(AccountSettingsEnum.emailUpdates.name());
		emailUpdatesSetting.setUserId(userId);
		emailUpdatesSetting.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
		emailUpdatesSetting.setValue(EmailFrequencyUpdates.NEVER.name()
				.toLowerCase());

		return emailUpdatesSetting;
	}

	public static AccountSetting createGravatarAvatarSetting(
			@NonNull final Long userId) {

		AccountSetting avatarSourceSetting = new AccountSetting();

		avatarSourceSetting.setName(AccountSettingsEnum.avatar.name());
		avatarSourceSetting.setUserId(userId);
		avatarSourceSetting.setPrivacy(Privacy.PUBLIC.name().toLowerCase());
		avatarSourceSetting.setValue(AvatarSourceEnum.GRAVATAR.name()
				.toLowerCase());
		return avatarSourceSetting;
	}

	public static List<AccountSetting> createLTIAvatarSetting(
			@NonNull final Long userId, @NonNull final String imageUrl) {

		AccountSetting avatarSourceSetting = new AccountSetting();

		avatarSourceSetting.setName(AccountSettingsEnum.avatar.name());
		avatarSourceSetting.setUserId(userId);
		avatarSourceSetting.setPrivacy(Privacy.PUBLIC.name());
		avatarSourceSetting.setValue(AvatarSourceEnum.LTI.name());

		AccountSetting avatarSetting = new AccountSetting();

		avatarSetting.setName(AccountSettingsEnum.lti_avatar.name());
		avatarSetting.setUserId(userId);
		avatarSetting.setPrivacy(Privacy.PUBLIC.name());
		avatarSetting.setValue(imageUrl);

		return Arrays.asList(avatarSourceSetting, avatarSetting);
	}

	public static AccountSetting createFacebookAvatarSetting(
			@NonNull final Long userId) {

		AccountSetting avatarSourceSetting = new AccountSetting();

		avatarSourceSetting.setName(AccountSettingsEnum.avatar.name());
		avatarSourceSetting.setUserId(userId);
		avatarSourceSetting.setPrivacy(Privacy.PUBLIC.name());
		avatarSourceSetting.setValue(AvatarSourceEnum.FACEBOOK.name());

		return avatarSourceSetting;
	}

	public static AccountSetting createGoogleAvatarSetting(
			@NonNull final Long userId) {
		AccountSetting avatarSourceSetting = new AccountSetting();

		avatarSourceSetting.setName(AccountSettingsEnum.avatar.name());
		avatarSourceSetting.setUserId(userId);
		avatarSourceSetting.setPrivacy(Privacy.PUBLIC.name());
		avatarSourceSetting.setValue(AvatarSourceEnum.GOOGLE.name());

		return avatarSourceSetting;
	}

	public static AccountSetting createTwitterAvatarSetting(
			@NonNull final Long userId) {
		AccountSetting avatarSourceSetting = new AccountSetting();

		avatarSourceSetting.setName(AccountSettingsEnum.avatar.name());
		avatarSourceSetting.setUserId(userId);
		avatarSourceSetting.setPrivacy(Privacy.PUBLIC.name());
		avatarSourceSetting.setValue(AvatarSourceEnum.TWITTER.name());

		return avatarSourceSetting;
	}

}
