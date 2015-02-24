package login.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class UserCredential {

	private Long userId;
	private String password;
	private boolean enabled;
	private LocalDateTime expires;

	public UserCredential() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDateTime getExpires() {
		return expires;
	}

	public void setExpires(LocalDateTime expires) {
		this.expires = expires;
	}

}
