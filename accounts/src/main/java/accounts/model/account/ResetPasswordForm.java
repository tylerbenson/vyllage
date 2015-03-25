package accounts.model.account;

import accounts.service.EmailValidator;

public class ResetPasswordForm {

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isValid() {
		return EmailValidator.validate(getEmail());
	}

}
