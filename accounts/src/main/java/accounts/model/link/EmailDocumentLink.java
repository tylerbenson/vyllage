package accounts.model.link;

import lombok.ToString;

/**
 * Object used to share a link for a document. Creating a user in the system for
 * the intended recipient. The userId is the user who has access to the
 * document.
 * 
 * @author uh
 *
 */
@ToString
public class EmailDocumentLink {
	private Long userId;
	private String generatedPassword;
	private Long documentId;
	private String documentType;
	private String linkKey;
	private LinkType linkType;
	private Long visits;

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

	public String getLinkKey() {
		return this.linkKey;
	}

	public void setLinkKey(String linkKey) {
		this.linkKey = linkKey;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	public Long getVisits() {
		return visits;
	}

	public void setVisits(Long visits) {
		this.visits = visits;
	}

}
