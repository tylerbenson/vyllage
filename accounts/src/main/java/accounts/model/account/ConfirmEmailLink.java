package accounts.model.account;

import lombok.ToString;

@ToString
public class ConfirmEmailLink {
	private Long userId;

	public ConfirmEmailLink() {
	}

	public ConfirmEmailLink(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
