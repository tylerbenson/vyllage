package connections.model;

import java.util.List;

import lombok.ToString;

@ToString
public class AdviceRequest {

	private List<AccountNames> users;

	private List<NotRegisteredUser> notRegisteredUsers;

	private String subject;

	private String message;

	private boolean allowGuestComments;

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

	public boolean getAllowGuestComments() {
		return allowGuestComments;
	}

	public void setAllowGuestComments(boolean allowGuestComments) {
		this.allowGuestComments = allowGuestComments;
	}
}
