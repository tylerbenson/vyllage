package accounts.model.account;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

@ToString
public class ChangePasswordForm {

	@NotNull
	@NotEmpty
	@Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters.")
	private String newPassword;

	@NotNull
	@NotEmpty
	@Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters.")
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
