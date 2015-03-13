package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class Suggestion {
	private Long suggestionId;
	private Long sectionId;
	private Long sectionVersion;
	private DocumentSection documentSection;
	private LocalDateTime lastModified;
	private Long userId;

	public Long getSuggestionId() {
		return suggestionId;
	}

	public void setSuggestionId(Long id) {
		this.suggestionId = id;
	}

	public DocumentSection getDocumentSection() {
		return documentSection;
	}

	public void setDocumentSection(DocumentSection documentSection) {
		this.documentSection = documentSection;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public Long getSectionVersion() {
		return sectionVersion;
	}

	public void setSectionVersion(Long sectionVersion) {
		this.sectionVersion = sectionVersion;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
