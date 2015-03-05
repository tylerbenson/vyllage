package accounts.model;

import lombok.ToString;

@ToString
public class UserFilterRequest {

	private String userName;

	public boolean isEmpty() {
		return userName == null || userName.isEmpty();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
