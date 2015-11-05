package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;
import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import documents.domain.tables.records.DocumentAccessRecord;
import documents.model.constants.DocumentAccessEnum;

@ToString
public class DocumentAccess {
	private Long documentId;
	private Long userId;

	// we don't need to send this outside right now.
	@JsonIgnore
	private DocumentAccessEnum access;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime dateCreated;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime expirationDate;

	private boolean allowGuestComments;

	private String userName;

	private String tagline;

	public DocumentAccess() {
	}

	public DocumentAccess(DocumentAccessRecord documentAccessRecord) {
		this.documentId = documentAccessRecord.getDocumentId();
		this.userId = documentAccessRecord.getUserId();
		this.access = DocumentAccessEnum.valueOf(documentAccessRecord
				.getAccess());
		this.dateCreated = documentAccessRecord.getDateCreated()
				.toLocalDateTime();
		this.lastModified = documentAccessRecord.getLastModified()
				.toLocalDateTime();
		this.expirationDate = documentAccessRecord.getExpirationDate() != null ? documentAccessRecord
				.getExpirationDate().toLocalDateTime() : null;
		this.allowGuestComments = documentAccessRecord.getAllowGuestComments();
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public DocumentAccessEnum getAccess() {
		return access;
	}

	public void setAccess(DocumentAccessEnum access) {
		this.access = access;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public boolean checkAccess(DocumentAccessEnum access) {

		// if access is the same or is WRITE and we are checking for READ then
		// ok too, if he can WRITE then he can also READ.
		return this.access.equals(access)
				|| (DocumentAccessEnum.WRITE.equals(this.access) && DocumentAccessEnum.READ
						.equals(access));
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean getAllowGuestComments() {
		return allowGuestComments;
	}

	public void setAllowGuestComments(boolean allowGuestComments) {
		this.allowGuestComments = allowGuestComments;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

}
