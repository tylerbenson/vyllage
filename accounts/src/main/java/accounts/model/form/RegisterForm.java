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

	boolean receiveAdvice;

	public boolean isValid() {
		return nameIsValid() && EmailValidator.isValid(getEmail())
				&& passwordIsValid();
	}

	protected boolean passwordIsValid() {
		return getPassword() != null && !getPassword().isEmpty()
				&& getPassword().length() >= 6;
	}

	protected boolean nameIsValid() {
		return getFirstName() != null && !getFirstName().isEmpty() && getLastName() != null
				&& !getLastName().isEmpty();
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

}
