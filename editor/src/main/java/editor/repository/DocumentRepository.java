package editor.repository;

import static editor.domain.editor.tables.Documents.DOCUMENTS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import editor.domain.editor.tables.records.DocumentsRecord;
import editor.model.Document;

@Repository
public class DocumentRepository {

	private final Logger logger = Logger.getLogger(DocumentRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Autowired
	private AccountRepository accountRepository;

	public Document get(Long id) {
		DocumentsRecord record = sql.fetchOne(DOCUMENTS, DOCUMENTS.ID.eq(id));
		logger.info("Searching document with id " + id);
		return recordToDocument(record);
	}

	public List<Document> getAll() {
		Result<DocumentsRecord> all = sql.fetch(DOCUMENTS);
		List<Document> allDocs = new ArrayList<>();

		for (DocumentsRecord documentsRecord : all)
			allDocs.add(recordToDocument(documentsRecord));

		return allDocs;
	}

	// TODO: move this code
	private Document recordToDocument(DocumentsRecord documentsRecord) {
		Document document = new Document();

		logger.info("document is null? " + (document == null));
		logger.info("documentsRecord is null? " + (documentsRecord == null));

		logger.info("Looking for account with id "
				+ documentsRecord.getAccountid());

		document.setId(documentsRecord.getId());
		document.setAccount(accountRepository.get(documentsRecord
				.getAccountid()));
		document.setDateCreated(documentsRecord.getDatecreated()
				.toLocalDateTime());
		document.setLastModified(documentsRecord.getLastmodified()
				.toLocalDateTime());
		document.setVisibility(documentsRecord.getVisibility());
		return document;
	}

	public Document save(Document document) {

		DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.ID.eq(document.getId()));

		if (existingRecord == null) {
			DocumentsRecord newRecord = sql.newRecord(DOCUMENTS);

			newRecord.setAccountid(document.getAccount().getId());
			newRecord.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setVisibility(document.getVisibility());

			newRecord.store();
			document.setId(newRecord.getId());
			document.setDateCreated(newRecord.getDatecreated()
					.toLocalDateTime());
			document.setLastModified(newRecord.getLastmodified()
					.toLocalDateTime());

		} else {

			existingRecord.setAccountid(document.getAccount().getId());
			existingRecord.setLastmodified(Timestamp.valueOf(LocalDateTime
					.now()));
			existingRecord.setVisibility(document.getVisibility());

			existingRecord.store();
			document.setLastModified(existingRecord.getLastmodified()
					.toLocalDateTime());
		}

		logger.info("Saved document: " + document);

		return document;
	}

	public void delete(Document document) {
		this.delete(document.getId());
	}

	public void delete(long documentId) {
		DocumentsRecord existingRecord = sql.fetchOne(DOCUMENTS,
				DOCUMENTS.ID.eq(documentId));
		existingRecord.delete(); // TODO: will this work with sections, etc?
	}

	// sql.insertInto(DOCUMENTS, DOCUMENTS.ID, //
	// DOCUMENTS.ACCOUNTID, //
	// DOCUMENTS.DATECREATED, //
	// DOCUMENTS.LASTMODIFIED, //
	// DOCUMENTS.VISIBILITY)//
	// .values(documentId, //
	// 0L, //
	// Timestamp.valueOf(LocalDateTime.now()), //
	// Timestamp.valueOf(LocalDateTime.now()), //
	// true).execute();

}
