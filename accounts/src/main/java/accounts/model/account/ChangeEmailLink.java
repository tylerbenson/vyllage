package accounts.model.account;

import lombok.ToString;

@ToString
public class ChangeEmailLink {

	private final Long userId;
	private final String newEmail;

	public ChangeEmailLink(Long userId, String newEmail) {
		super();
		this.userId = userId;
		this.newEmail = newEmail;
	}

}
