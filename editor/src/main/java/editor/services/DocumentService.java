package editor.services;

import static editor.domain.public_.tables.DocumentSections.DOCUMENT_SECTIONS;
import static editor.domain.public_.tables.Documents.DOCUMENTS;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import editor.model.DocumentSection;

/**
 * This service takes care of saving and retrieving documents.
 * 
 * @author uh
 *
 */
@Service
public class DocumentService {

	private final Logger logger = Logger.getLogger(DocumentService.class
			.getName());

	@Autowired
	private DSLContext sql;

	/**
	 * Saves the json documentSection, if the record is already present it will
	 * update instead.
	 * 
	 * @param body
	 * @throws JsonProcessingException
	 */
	public void saveDocumentSection(Long documentId, DocumentSection body)
			throws JsonProcessingException {

		// TODO: replace id with document?
		Result<Record> documentSections = sql.select().from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.ID.equal(body.getSectionId()))
				.and(DOCUMENT_SECTIONS.DOCUMENTID.eq(documentId)).fetch();

		logger.info("Saving document: " + body.getSectionId());

		// TODO: handle version instead of updating the same document, link to
		// an actual document, obtain sort order from somewhere, etc.
		// Refactor to save a list of sections?

		int execute;

		if (documentSections.isEmpty()) {
			// TODO: get account id

			sql.insertInto(DOCUMENTS, DOCUMENTS.ID, //
					DOCUMENTS.ACCOUNTID, //
					DOCUMENTS.DATECREATED, //
					DOCUMENTS.LASTMODIFIED, //
					DOCUMENTS.VISIBILITY) //
					.values(documentId, //
							0L, //
							Timestamp.valueOf(LocalDateTime.now()), //
							Timestamp.valueOf(LocalDateTime.now()), //
							true).execute();

			execute = sql
					.insertInto(DOCUMENT_SECTIONS,
							DOCUMENT_SECTIONS.ID, //
							DOCUMENT_SECTIONS.SECTIONVERSION,
							DOCUMENT_SECTIONS.JSONDOCUMENT, //
							DOCUMENT_SECTIONS.DOCUMENTID, //
							DOCUMENT_SECTIONS.DATECREATED, //
							DOCUMENT_SECTIONS.LASTMODIFIED, //
							DOCUMENT_SECTIONS.SORTORDER)
					.values(body.getSectionId(), //
							1L, body.asJSON(), //
							documentId, //
							Timestamp.valueOf(LocalDateTime.now()), //
							Timestamp.valueOf(LocalDateTime.now()), //
							Sort.DSC.name()).execute();
		} else {
			logger.info("Records found: " + documentSections.size());

			execute = sql
					.update(DOCUMENT_SECTIONS)
					.set(DOCUMENT_SECTIONS.JSONDOCUMENT, body.asJSON())
					.set(DOCUMENT_SECTIONS.LASTMODIFIED,
							Timestamp.valueOf(LocalDateTime.now()))
					.where(DOCUMENT_SECTIONS.ID.equal(body.getSectionId()))
					.execute();

		}

		logger.info("Inserted " + execute + " records.");

	}

	/**
	 * @param id
	 * @param sectionId
	 * @return DocumentSection
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public DocumentSection getDocument(Long documentId, Long sectionId)
			throws JsonParseException, JsonMappingException, IOException {

		// TODO: handle exceptions, not found, etc

		Result<Record> records = sql
				.select()
				.from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.ID.equal(sectionId).and(
						DOCUMENT_SECTIONS.DOCUMENTID.equal(documentId)))
				.fetch();

		logger.info("Records found: " + records.size());

		String json = records.get(0).getValue(DOCUMENT_SECTIONS.JSONDOCUMENT);

		return DocumentSection.fromJSON(json);
	}

	public void saveDocument(Long is) {

	}

	public DocumentSection getDocumentSections(Long documentId) {
		// TODO Auto-generated method stub
		return null;
	}

}
