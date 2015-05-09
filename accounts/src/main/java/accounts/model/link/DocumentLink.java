package accounts.model.link;

import lombok.ToString;

/**
 * Object used to share a link for a document. Creating a user in the system for
 * the intended recipient.
 * 
 * @author uh
 *
 */
@ToString
public class DocumentLink {
	private Long userId;
	private String generatedPassword;
	private Long documentId;
	private String documentType;

	/**
	 * 
	 * @return
	 */
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
