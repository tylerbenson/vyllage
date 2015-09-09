package accounts.model.account.settings;

import lombok.ToString;

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

}
