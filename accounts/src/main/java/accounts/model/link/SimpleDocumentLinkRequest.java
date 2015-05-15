package accounts.model.link;

import lombok.ToString;

@ToString
public class SimpleDocumentLinkRequest {

	private Long documentId;

	private String documentType;

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
