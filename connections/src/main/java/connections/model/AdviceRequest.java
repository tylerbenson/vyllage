package connections.model;

import java.util.List;

import lombok.ToString;

@ToString
public class AdviceRequest {

	private List<AccountContact> registeredUsersContactData;

	private List<NotRegisteredUser> notRegisteredUsers;

	private String senderName;

	public List<AccountContact> getRegisteredUsersContatData() {
		return registeredUsersContactData;
	}

	public void setRegisteredUsersContatData(
			List<AccountContact> registeredUsers) {
		this.registeredUsersContactData = registeredUsers;
	}

	public List<NotRegisteredUser> getNotRegisteredUsers() {
		return notRegisteredUsers;
	}

	public void setNotRegisteredUsers(List<NotRegisteredUser> notRegisteredUsers) {
		this.notRegisteredUsers = notRegisteredUsers;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderName() {
		return this.senderName;
	}

}
