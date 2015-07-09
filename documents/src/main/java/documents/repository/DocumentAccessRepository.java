package documents.repository;

import static documents.domain.tables.DocumentAccess.DOCUMENT_ACCESS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user.common.User;
import documents.domain.tables.Documents;
import documents.domain.tables.records.DocumentAccessRecord;
import documents.model.DocumentAccess;

@Repository
public class DocumentAccessRepository {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(DocumentAccessRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public Optional<DocumentAccess> get(Long userId, Long documentId) {
		Result<DocumentAccessRecord> result = sql.fetch(
				DOCUMENT_ACCESS,
				DOCUMENT_ACCESS.USER_ID.eq(userId).and(
						DOCUMENT_ACCESS.DOCUMENT_ID.eq(documentId)));

		if (result != null && result.isNotEmpty())
			return Optional.ofNullable(new DocumentAccess(result.get(0)));
		return Optional.empty();
	}

	public void create(DocumentAccess documentAccess) {

		Result<DocumentAccessRecord> result = sql.fetch(
				DOCUMENT_ACCESS,
				DOCUMENT_ACCESS.USER_ID.eq(documentAccess.getUserId()).and(
						DOCUMENT_ACCESS.DOCUMENT_ID.eq(documentAccess
								.getDocumentId())));

		// if exists call update instead...
		if (result != null && result.isNotEmpty()) {
			this.update(documentAccess);
			return;
		}

		DocumentAccessRecord newRecord = sql.newRecord(DOCUMENT_ACCESS);
		newRecord.setAccess(documentAccess.getAccess().name());
		newRecord.setDocumentId(documentAccess.getDocumentId());
		newRecord.setUserId(documentAccess.getUserId());
		newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
				.of("UTC"))));
		newRecord.setLastModified(Timestamp.valueOf(LocalDateTime.now(ZoneId
				.of("UTC"))));
		newRecord.setExpirationDate(getExpirationDateOrNull(documentAccess));

		newRecord.insert();
	}

	public void update(DocumentAccess documentAccess) {

		Result<DocumentAccessRecord> result = sql.fetch(
				DOCUMENT_ACCESS,
				DOCUMENT_ACCESS.USER_ID.eq(documentAccess.getUserId()).and(
						DOCUMENT_ACCESS.DOCUMENT_ID.eq(documentAccess
								.getDocumentId())));

		DocumentAccessRecord documentAccessRecord = result.get(0);

		documentAccessRecord.setAccess(documentAccess.getAccess().name());
		documentAccessRecord.setLastModified(Timestamp.valueOf(LocalDateTime
				.now(ZoneId.of("UTC"))));
		documentAccessRecord
				.setExpirationDate(getExpirationDateOrNull(documentAccess));

		documentAccessRecord.update();
	}

	public void delete(DocumentAccess documentAccess) {
		Result<DocumentAccessRecord> result = sql.fetch(
				DOCUMENT_ACCESS,
				DOCUMENT_ACCESS.DOCUMENT_ID.eq(documentAccess.getDocumentId())
						.and(DOCUMENT_ACCESS.USER_ID.eq(documentAccess
								.getUserId())));

		if (result != null && result.isNotEmpty())
			result.forEach(r -> r.delete());
	}

	/**
	 * Returns all permissions for a given document.
	 * 
	 * @param documentId
	 * @return
	 */
	public List<DocumentAccess> get(Long documentId) {
		Result<DocumentAccessRecord> result = sql.fetch(DOCUMENT_ACCESS,
				DOCUMENT_ACCESS.DOCUMENT_ID.eq(documentId));

		if (result == null || result.isEmpty())
			return Collections.emptyList();
		return result.stream().map(DocumentAccess::new)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all the user's document access permissions he granted.
	 * 
	 * @param user
	 * @return
	 */
	public List<DocumentAccess> getFromUserDocuments(User user) {
		documents.domain.tables.DocumentAccess da = DOCUMENT_ACCESS.as("da");
		Documents docs = Documents.DOCUMENTS.as("docs");
		List<DocumentAccessRecord> result = sql.select().from(da).join(docs)
				.on(docs.DOCUMENT_ID.eq(da.DOCUMENT_ID))
				.where(docs.USER_ID.eq(user.getUserId()))
				.fetchInto(DocumentAccessRecord.class);

		if (result == null || result.isEmpty())
			return Collections.emptyList();
		return result.stream().map(DocumentAccess::new)
				.collect(Collectors.toList());
	}

	private Timestamp getExpirationDateOrNull(DocumentAccess documentAccess) {
		if (documentAccess.getExpirationDate() == null)
			return null;

		return Timestamp.valueOf(documentAccess.getExpirationDate());
	}

}
