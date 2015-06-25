package accounts.model.link;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class AbstractDocumentLink {

	protected Long userId;
	protected Long documentId;
	protected String documentType;
	protected String linkKey;
	protected LinkType linkType;
	protected Long visits;

	public AbstractDocumentLink() {
		super();
	}

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

	public String getLinkKey() {
		return linkKey;
	}

	public void setLinkKey(String linkKey) {
		this.linkKey = linkKey;
	}

}