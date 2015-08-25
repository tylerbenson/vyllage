package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;
import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import documents.model.document.sections.DocumentSection;

@ToString
public class Suggestion {
	private Long suggestionId;
	private Long sectionId;
	private Long sectionVersion;
	private DocumentSection documentSection;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;
	private Long userId;

	// not saved in the DB
	private String userName;

	// not saved in the DB
	private String avatarUrl;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}
