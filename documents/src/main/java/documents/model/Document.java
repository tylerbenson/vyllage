package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class Document {
	private Long documentId;
	private String tagline;
	private Long userId;
	private Boolean visibility;
	private LocalDateTime dateCreated;
	private LocalDateTime lastModified;
	private String documentType;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public Long getDocumentId() {
		return this.documentId;
	}

	public void setDocumentId(Long id) {
		this.documentId = id;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getDateCreated() {
		return this.dateCreated;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public LocalDateTime getLastModified() {
		return this.lastModified;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String getDocumentType() {
		return this.documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

}
