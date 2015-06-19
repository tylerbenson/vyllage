package accounts.model.form;

import user.common.User;

/**
 * Represents a user for the forms
 * 
 * @author uh
 *
 */
public class UserFormObject {

	private Long userId;
	private String username;

	public UserFormObject(User user) {
		super();
		this.userId = user.getUserId();
		this.username = user.getUsername();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
