package connections.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.ToString;

@ToString
public class AdviceRequestParameter {

	private List<AccountContact> registeredUsersContactData;

	private List<NotRegisteredUser> notRegisteredUsers;

	private String senderName;

	private Long userId;

	private Long documentId;

	private LocalDateTime linkExpirationDate;

	private CSRFToken CSRFToken;

	public List<AccountContact> getRegisteredUsersContactData() {
		return registeredUsersContactData;
	}

	public void setRegisteredUsersContactData(
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

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public LocalDateTime getLinkExpirationDate() {
		return linkExpirationDate;
	}

	public void setLinkExpirationDate(LocalDateTime linkExpirationDate) {
		this.linkExpirationDate = linkExpirationDate;
	}

	public CSRFToken getCSRFToken() {
		return CSRFToken;
	}

	public void setCSRFToken(CSRFToken cSRFToken) {
		CSRFToken = cSRFToken;
	}

}
