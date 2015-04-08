package accounts.model;

import lombok.ToString;

@ToString
public class UserEmail {
	private Long userId;
	private String email;

	public UserEmail(Long userId, String username) {
		this.userId = userId;
		email = username;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
