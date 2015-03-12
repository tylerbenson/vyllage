package documents.repository;

import static documents.domain.tables.Comments.COMMENTS;
import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;
import static documents.domain.tables.Documents.DOCUMENTS;
import static documents.domain.tables.Suggestions.SUGGESTIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import documents.domain.tables.Comments;
import documents.domain.tables.DocumentSections;
import documents.domain.tables.Suggestions;
import documents.domain.tables.records.DocumentsRecord;
import documents.model.Document;

@Repository
public class DocumentRepository implements IRepository<Document> {

	private final Logger logger = Logger.getLogger(DocumentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public Document get(Long id) throws ElementNotFoundException {
		DocumentsRecord record = sql.fetchOne(DOCUMENTS, DOCUMENTS.ID.eq(id));
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

		document.setId(documentsRecord.getId());
		document.setUserId(documentsRecord.getUserid());
		document.setDateCreated(documentsRecord.getDatecreated()
				.toLocalDateTime());
		document.setLastModified(documentsRecord.getLastmodified()
				.toLocalDateTime());
		document.setVisibility(documentsRecord.getVisibility());
		document.setTagline(documentsRecord.getTagline());
		return document;
	}

	@Override
	public Document save(Document document) {

		DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.ID.eq(document.getId()));

		if (existingRecord == null) {
			DocumentsRecord newRecord = sql.newRecord(DOCUMENTS);

			newRecord.setUserid(document.getUserId());
			newRecord.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setVisibility(document.getVisibility());
			newRecord.setTagline(document.getTagline());

			newRecord.store();
			document.setId(newRecord.getId());
			document.setDateCreated(newRecord.getDatecreated()
					.toLocalDateTime());
			document.setLastModified(newRecord.getLastmodified()
					.toLocalDateTime());

		} else {

			existingRecord.setUserid(document.getUserId());
			existingRecord.setLastmodified(Timestamp.valueOf(LocalDateTime
					.now()));
			existingRecord.setVisibility(document.getVisibility());
			existingRecord.setTagline(document.getTagline());
			existingRecord.update();
			document.setLastModified(existingRecord.getLastmodified()
					.toLocalDateTime());
		}

		logger.info("Saved document: " + document);

		return document;
	}

	public void delete(Document document) {
		this.delete(document.getId());
	}

	@Override
	public void delete(Long documentId) {
		if (documentId != null) {
			DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
					DOCUMENTS.ID.eq(documentId));
			existingRecord.delete();
		}
	}

	public Document getDocumentByUser(Long userId)
			throws ElementNotFoundException {
		DocumentsRecord record = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.USERID.eq(userId));
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
		Suggestions sg = SUGGESTIONS.as("sg");

		Result<Record2<Long, Long>> result = sql
				.select(c.USER_ID, sg.USER_ID)
				//
				.from(s1)
				//
				.join(s2)
				//
				.on(s1.ID.eq(s2.ID))
				//
				.and(s1.SECTIONVERSION.lessOrEqual(s2.SECTIONVERSION))
				//
				.join(c).on(c.SECTION_ID.eq(s1.ID)).join(sg)
				.on(sg.SECTION_ID.eq(s1.ID))
				.where(s1.DOCUMENTID.eq(documentId)).fetch();

		return result
				.stream()
				.map(r -> Arrays.asList(r.getValue(c.USER_ID),
						r.getValue(sg.USER_ID))).flatMap(l -> l.stream())
				.distinct().collect(Collectors.toList());

	}

}
