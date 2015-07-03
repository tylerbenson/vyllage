package documents.model;

import java.time.LocalDateTime;

import lombok.ToString;
import documents.domain.tables.records.DocumentAccessRecord;
import documents.model.constants.DocumentAccessEnum;

@ToString
public class DocumentAccess {
	private Long documentId;
	private Long userId;
	private DocumentAccessEnum access;
	private LocalDateTime dateCreated;
	private LocalDateTime lastModified;

	public DocumentAccess() {
	}

	public DocumentAccess(DocumentAccessRecord documentAccessRecord) {
		this.documentId = documentAccessRecord.getDocumentId();
		this.documentId = documentAccessRecord.getUserId();
		this.access = DocumentAccessEnum.valueOf(documentAccessRecord
				.getAccess());
		this.dateCreated = documentAccessRecord.getDateCreated()
				.toLocalDateTime();
		this.lastModified = documentAccessRecord.getLastModified()
				.toLocalDateTime();
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
}
