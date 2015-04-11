package connections.model;

import java.util.List;

public class AdviceRequest {

	private CSRFToken CSRFToken;

	private List<AccountNames> users;

	private List<NotRegisteredUser> notRegisteredUsers;

	private String subject;

	private String message;

	public List<AccountNames> getUsers() {
		return users;
	}

	public void setUsers(List<AccountNames> users) {
		this.users = users;
	}

	public List<NotRegisteredUser> getNotRegisteredUsers() {
		return notRegisteredUsers;
	}

	public void setNotRegisteredUsers(List<NotRegisteredUser> notRegisteredUsers) {
		this.notRegisteredUsers = notRegisteredUsers;
	}

	public CSRFToken getCSRFToken() {
		return CSRFToken;
	}

	public void setCSRFToken(CSRFToken cSRFToken) {
		CSRFToken = cSRFToken;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
