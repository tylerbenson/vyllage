package documents.repository;

import static documents.domain.tables.Comments.COMMENTS;
import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;
import static documents.domain.tables.Documents.DOCUMENTS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user.common.User;
import documents.domain.tables.Comments;
import documents.domain.tables.DocumentSections;
import documents.domain.tables.records.DocumentsRecord;
import documents.model.Document;
import documents.model.constants.DocumentTypeEnum;

@Repository
public class DocumentRepository implements IRepository<Document> {

	private final Logger logger = Logger.getLogger(DocumentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	private DocumentSectionRepository documentSectionRepository;

	@Inject
	public DocumentRepository(
			DocumentSectionRepository documentSectionRepository) {
		this.documentSectionRepository = documentSectionRepository;
	}

	@Override
	public Document get(Long id) throws ElementNotFoundException {
		DocumentsRecord record = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.DOCUMENT_ID.eq(id));
		logger.info("Searching document with id " + id);

		if (record == null)
			throw new ElementNotFoundException("Document with id '" + id
					+ "' could not be found.");

		return recordToDocument(record);
	}

	@Override
	public List<Document> getAll() {
		Result<DocumentsRecord> all = sql.fetch(DOCUMENTS);
		List<Document> allDocs = new ArrayList<>();

		for (DocumentsRecord documentsRecord : all)
			allDocs.add(recordToDocument(documentsRecord));

		return allDocs;
	}

	private Document recordToDocument(DocumentsRecord documentsRecord) {
		Document document = new Document();

		document.setDocumentId(documentsRecord.getDocumentId());
		document.setUserId(documentsRecord.getUserId());
		document.setDateCreated(documentsRecord.getDateCreated()
				.toLocalDateTime());
		document.setLastModified(documentsRecord.getLastModified()
				.toLocalDateTime());
		document.setVisibility(documentsRecord.getVisibility());
		document.setTagline(documentsRecord.getTagline());
		document.setDocumentType(documentsRecord.getDocumentType());
		return document;
	}

	@Override
	public Document save(Document document) {

		DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.DOCUMENT_ID.eq(document.getDocumentId()));

		if (existingRecord == null) {
			DocumentsRecord newRecord = sql.newRecord(DOCUMENTS);

			newRecord.setUserId(document.getUserId());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));
			newRecord.setVisibility(document.getVisibility());
			newRecord.setTagline(document.getTagline());
			newRecord.setDocumentType(document.getDocumentType());

			newRecord.store();
			document.setDocumentId(newRecord.getDocumentId());
			document.setDateCreated(newRecord.getDateCreated()
					.toLocalDateTime());
			document.setLastModified(newRecord.getLastModified()
					.toLocalDateTime());

		} else {

			existingRecord.setUserId(document.getUserId());
			existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));
			existingRecord.setVisibility(document.getVisibility());
			existingRecord.setTagline(document.getTagline());
			existingRecord.update();
			document.setLastModified(existingRecord.getLastModified()
					.toLocalDateTime());
		}

		logger.info("Saved document: " + document);

		return document;
	}

	@Override
	public void delete(Long documentId) {
		if (documentId != null) {
			DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
					DOCUMENTS.DOCUMENT_ID.eq(documentId));
			existingRecord.delete();
		}
	}

	public Document getDocumentByUser(Long userId)
			throws ElementNotFoundException {
		DocumentsRecord record = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.USER_ID.eq(userId));
		logger.info("Searching document with userid " + userId);

		if (record == null)
			throw new ElementNotFoundException("Document for user id '"
					+ userId + "' could not be found.");

		return recordToDocument(record);

	}

	public List<Long> getRecentUsersForDocument(Long documentId) {
		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");
		Comments c = COMMENTS.as("c");

		Result<Record1<Long>> result = sql.select(c.USER_ID) //
				.from(s1) //
				.join(s2) //
				.on(s1.ID.eq(s2.ID)) //
				.and(s1.SECTIONVERSION.lessOrEqual(s2.SECTIONVERSION)) //
				.join(c) //
				.on(c.SECTION_ID.eq(s1.ID)) //
				.where(s1.DOCUMENTID.eq(documentId)).groupBy(c.USER_ID).fetch(); //

		return result.stream().map(r -> Arrays.asList(r.getValue(c.USER_ID)))
				.flatMap(l -> l.stream()).distinct()
				.collect(Collectors.toList());

	}

	public void deleteForUser(Long userId) {
		DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.USER_ID.eq(userId));

		existingRecord.delete();
	}

	/**
	 * Checks if a given document id exists for a given user. A document will
	 * exist for a given user if, it exists, has sections and the user owns the
	 * document.
	 *
	 * If a document exists, has no sections but the user is the owner then it
	 * just means he has not added any sections yet.
	 *
	 * @param user
	 * @param documentId
	 * @return
	 */
	public boolean existsForUser(User user, Long documentId) {
		boolean exists = true;

		DocumentsRecord documentRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.DOCUMENT_ID.eq(documentId));

		if (documentRecord == null)
			return !exists;

		boolean hasSections = documentSectionRepository.exists(documentId);

		boolean userIsOwner = user.getUserId().equals(
				documentRecord.getUserId());

		if (!userIsOwner && !hasSections)
			return !exists;

		if (userIsOwner && !hasSections)
			return exists;

		return exists && hasSections;
	}

	/**
	 * Checks that a document exists.
	 *
	 * @param documentId
	 * @return
	 */
	public boolean exists(Long documentId) {
		boolean exists = true;

		DocumentsRecord documentRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.DOCUMENT_ID.eq(documentId));

		if (documentRecord == null)
			return !exists;
		return exists;
	}

	public List<Document> getDocumentByUserAndType(Long userId,
			DocumentTypeEnum documentTypeEnum) {
		Result<DocumentsRecord> result = sql.fetch(
				DOCUMENTS,
				DOCUMENTS.USER_ID.eq(userId).and(
						DOCUMENTS.DOCUMENT_TYPE.contains(documentTypeEnum
								.name())));

		List<Document> allDocs = new ArrayList<>();
		for (DocumentsRecord documentsRecord : result)
			allDocs.add(recordToDocument(documentsRecord));

		return allDocs;
	}

	public Map<Long, String> getTaglines(List<Long> userIds) {
		Result<Record> fetch = sql.select().from(DOCUMENTS)
				.where(DOCUMENTS.USER_ID.in(userIds)).fetch();

		if (fetch == null || fetch.isEmpty())
			return Collections.emptyMap();

		Map<Long, String> taglinesById = new HashMap<>();
		fetch.stream().forEach(
				r -> taglinesById.put(r.getValue(DOCUMENTS.USER_ID),
						r.getValue(DOCUMENTS.TAGLINE)));

		return taglinesById;
	}

}
