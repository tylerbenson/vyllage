package accounts.model.account;

import lombok.ToString;

@ToString
public class ResetPasswordLink {
	private Long userId;
	private String randomPassword;

	public String getRandomPassword() {
		return randomPassword;
	}

	public void setRandomPassword(String randomPassword) {
		this.randomPassword = randomPassword;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
