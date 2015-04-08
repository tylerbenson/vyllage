package accounts.model.account;

import lombok.ToString;

import org.hibernate.validator.constraints.Email;

import accounts.validation.EmailValidator;

@ToString
public class ResetPasswordForm {

	@Email
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
