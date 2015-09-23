package documents.model;

import java.time.LocalDateTime;

import lombok.NonNull;
import lombok.ToString;
import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import documents.model.document.sections.DocumentSection;

@ToString
public class SectionAdvice {
	private Long sectionAdviceId;
	private Long sectionId;
	private Long sectionVersion;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;
	private Long userId;

	// not saved in the DB
	private String userName;

	// not saved in the DB
	private String avatarUrl;

	private DocumentSection documentSection;

	// accepted, rejected, pending, handled by the frontend.
	private String status;

	public Long getSectionAdviceId() {
		return sectionAdviceId;
	}

	public void setSectionAdviceId(Long id) {
		this.sectionAdviceId = id;
	}

	public DocumentSection getDocumentSection() {
		return documentSection;
	}

	public void setDocumentSection(@NonNull DocumentSection documentSection) {
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

	public void setSectionId(@NonNull Long sectionId) {
		this.sectionId = sectionId;
	}

	public Long getSectionVersion() {
		return sectionVersion;
	}

	public void setSectionVersion(@NonNull Long sectionVersion) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
