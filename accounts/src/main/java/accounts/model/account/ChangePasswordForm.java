package accounts.model.account;

import lombok.ToString;

@ToString
public class ChangePasswordForm {

	private String newPassword;

	private String confirmPassword;

	private boolean error = false;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isValid() {
		return newPassword != null && !newPassword.isEmpty()
				&& newPassword.equalsIgnoreCase(confirmPassword);
	}

	public boolean hasErrors() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
}
