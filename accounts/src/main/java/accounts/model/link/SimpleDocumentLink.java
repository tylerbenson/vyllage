package accounts.model.link;

import lombok.ToString;

/**
 * Object used to share a document link without creating a new user. Used to
 * share a link to a document to persons outside the system. The user id is to
 * get information about the user who create the link later.
 * 
 * @author uh
 *
 */
@ToString
public class SimpleDocumentLink {

	private Long userId;
	private Long documentId;
	private String documentType;

	public Long getUserId() {
		return userId;
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

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

}
