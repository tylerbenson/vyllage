package connections.model;

import java.util.List;

import lombok.ToString;
import user.common.web.AccountContact;

@ToString
public class AdviceRequest {

	private List<AccountContact> users;

	private List<NotRegisteredUser> notRegisteredUsers;

	private String subject;

	private String message;

	private boolean allowGuestComments;

	public List<AccountContact> getUsers() {
		return users;
	}

	public void setUsers(List<AccountContact> users) {
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
