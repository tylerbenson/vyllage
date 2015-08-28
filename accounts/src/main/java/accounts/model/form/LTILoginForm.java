package accounts.model.form;

import org.jooq.tools.StringUtils;

public class LTILoginForm {

	private String email;
	private String password;
	private boolean error;

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

	public boolean isValid() {
		if (StringUtils.isBlank(email) && StringUtils.isBlank(password))
			this.error = true;

		return !this.error;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

}
