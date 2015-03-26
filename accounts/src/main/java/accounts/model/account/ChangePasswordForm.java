package accounts.model.account;

import lombok.ToString;

@ToString
public class ChangePasswordForm {
	private String newPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public boolean isValid() {
		return newPassword != null && !newPassword.isEmpty();
	}
}
