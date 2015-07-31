package accounts.model.account;

import lombok.ToString;

@ToString
public class ChangeEmailLink {

	private Long userId;
	private String newEmail;

	public ChangeEmailLink() {

	}

	public ChangeEmailLink(Long userId, String newEmail) {
		super();
		this.setUserId(userId);
		this.setNewEmail(newEmail);
	}

	public Long getUserId() {
		return userId;
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

}
