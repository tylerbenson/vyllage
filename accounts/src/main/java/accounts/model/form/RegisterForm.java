package accounts.model.form;

import lombok.ToString;
import accounts.validation.EmailValidator;

/**
 * Form to register a new user.
 *
 * @author uh
 *
 */
@ToString
public class RegisterForm {

	private String firstName;
	private String lastName;
	private String email;
	private String password;

	private boolean receiveAdvice;

	private String errorMsg;

	public boolean isValid() {
		return nameIsValid() && emailIsValid() && passwordIsValid();
	}

	public boolean emailIsValid() {
		boolean emailIsValid = EmailValidator.isValid(getEmail());

		if (!emailIsValid)
			setErrorMsg("Invalid email address.");

		return emailIsValid;
	}

	public boolean passwordIsValid() {

		boolean isValid = getPassword() != null && !getPassword().isEmpty()
				&& getPassword().length() >= 6;
		if (!isValid)
			setErrorMsg("Invalid password. Cannot be empty and/or shorter than 6 characters.");

		return isValid;
	}

	public boolean nameIsValid() {

		boolean isFirstNameValid = getFirstName() != null
				&& !getFirstName().isEmpty();
		boolean isLastNameValid = getLastName() != null
				&& !getLastName().isEmpty();

		if (!isFirstNameValid)
			setErrorMsg("First name can't be empty");

		if (!isLastNameValid)
			setErrorMsg("Last name can't be empty");

		return isFirstNameValid && isLastNameValid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean getReceiveAdvice() {
		return receiveAdvice;
	}

	public void setReceiveAdvice(boolean receiveAdvice) {
		this.receiveAdvice = receiveAdvice;
	}

}
