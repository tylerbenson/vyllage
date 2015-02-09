package editor.repository;

import static editor.domain.editor.tables.DocumentSections.DOCUMENT_SECTIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.model.DocumentSection;

@Repository
public class DocumentSectionRepository {

	private final Logger logger = Logger
			.getLogger(DocumentSectionRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public List<String> getDocumentSections(Long documentId) {
		Result<Record> documentSections = sql.select().from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.DOCUMENTID.eq(documentId)).fetch();

		return documentSections.stream()
				.map(r -> r.getValue(DOCUMENT_SECTIONS.JSONDOCUMENT))
				.collect(Collectors.toList());
	}

	/**
	 * Saves a section of a document
	 * 
	 * @param documentId
	 * @param body
	 * @return
	 * @throws JsonProcessingException
	 */
	public void insertDocumentSection(Long documentId, DocumentSection body)
			throws JsonProcessingException {
		int execute;
		execute = sql
				.insertInto(DOCUMENT_SECTIONS,
						DOCUMENT_SECTIONS.ID, //
						DOCUMENT_SECTIONS.SECTIONVERSION,
						DOCUMENT_SECTIONS.JSONDOCUMENT, //
						DOCUMENT_SECTIONS.DOCUMENTID, //
						DOCUMENT_SECTIONS.DATECREATED, //
						DOCUMENT_SECTIONS.LASTMODIFIED, //
						DOCUMENT_SECTIONS.POSITION).values(body.getSectionId(), //
						1L, body.asJSON(), //
						documentId, //
						Timestamp.valueOf(LocalDateTime.now()), //
						Timestamp.valueOf(LocalDateTime.now()), //
						1L).execute();

		logger.info("Inserted " + execute + " records.");
	}

	public void updateDocumentSection(DocumentSection body)
			throws JsonProcessingException {
		sql.update(DOCUMENT_SECTIONS)
				.set(DOCUMENT_SECTIONS.JSONDOCUMENT, body.asJSON())
				.set(DOCUMENT_SECTIONS.LASTMODIFIED,
						Timestamp.valueOf(LocalDateTime.now()))
				.where(DOCUMENT_SECTIONS.ID.equal(body.getSectionId()))
				.execute();
	}

	public String getSection(Long documentId, Long sectionId)
			throws DocumentSectionNotFoundException {
		Result<Record> records = sql
				.select()
				.from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.ID.equal(sectionId).and(
						DOCUMENT_SECTIONS.DOCUMENTID.equal(documentId)))
				.fetch();

		logger.info("Records found: " + records.size());

		String value;

		try {
			value = records.get(0).getValue(DOCUMENT_SECTIONS.JSONDOCUMENT);

		} catch (IndexOutOfBoundsException e) {
			throw new DocumentSectionNotFoundException("Section " + sectionId
					+ " from document " + documentId + " not found.");
		}

		return value;
	}

}
