package accounts.model.account;

import lombok.ToString;
import accounts.service.EmailValidator;

@ToString
public class ResetPasswordForm {

	private String email;

	private boolean error = false;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isValid() {
		return EmailValidator.validate(getEmail());
	}

	public boolean hasErrors() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

}
