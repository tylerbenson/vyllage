package accounts.repository;

import lombok.ToString;

import org.springframework.security.authentication.AccountStatusException;

@ToString
public class PasswordResetWasForcedException extends AccountStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3022902039825417428L;
	private Long userId;
	private String userName;

	public PasswordResetWasForcedException(String msg, Long userId, String userName) {
		super(msg);
		this.userId = userId;
		this.userName = userName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
