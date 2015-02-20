package login.model.link;

import lombok.ToString;

@ToString
public class DocumentLink {
	private String userName;
	private String generatedPassword;
	private Long documentId;
	private String documentType;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGeneratedPassword() {
		return generatedPassword;
	}

	public void setGeneratedPassword(String generatedPassword) {
		this.generatedPassword = generatedPassword;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
}
