package accounts.repository;

import lombok.ToString;

import org.springframework.security.authentication.AccountStatusException;

import user.common.User;

@ToString
public class PasswordResetWasForcedException extends AccountStatusException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3022902039825417428L;
	private final User user;

	public PasswordResetWasForcedException(String msg, User user) {
		super(msg);
		this.user = user;
	}

	public Long getUserId() {
		return getUser().getUserId();
	}

	public String getUserName() {
		return getUser().getUsername();
	}

	public User getUser() {
		return user;
	}

}
