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

import editor.domain.editor.tables.records.DocumentSectionsRecord;
import editor.model.Document;
import editor.model.DocumentSection;

@Repository
public class DocumentSectionRepository {

	private final Logger logger = Logger
			.getLogger(DocumentSectionRepository.class.getName());

	@Autowired
	private DSLContext sql;

	@Autowired
	private DocumentRepository documentRepository;

	public DocumentSection get(Long id) throws DocumentSectionNotFoundException {
		DocumentSectionsRecord existingRecord = sql.fetchOne(DOCUMENT_SECTIONS,
				DOCUMENT_SECTIONS.ID.eq(id));

		if (existingRecord == null)
			throw new DocumentSectionNotFoundException(
					"DocumentSection with id '" + id + "' not found.");

		return DocumentSection.fromJSON(existingRecord.getJsondocument());
	}

	/**
	 * 
	 * @param documentId
	 *            the id of the related document
	 * @return
	 * @throws DocumentSectionNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Long documentId)
			throws DocumentSectionNotFoundException {

		Result<Record> documentSections = sql.select().from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.DOCUMENTID.eq(documentId)).fetch();

		if (documentSections == null)
			throw new DocumentSectionNotFoundException(
					"DocumentSection from Document id '" + documentId
							+ "' not found.");

		return documentSections.stream()
				.map(r -> r.getValue(DOCUMENT_SECTIONS.JSONDOCUMENT))
				.map(DocumentSection::fromJSON).collect(Collectors.toList());
	}

	/**
	 * Returns all the sections of the document
	 * 
	 * @param document
	 * @return DocumentSection
	 * @throws DocumentSectionNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Document document)
			throws DocumentSectionNotFoundException {

		return this.getDocumentSections(document.getId());
	}

	public DocumentSection save(Document document,
			DocumentSection documentSection) throws JsonProcessingException {

		if (documentRepository.get(document.getId()) == null) {
			logger.info("Document not found, saving document first.");
			document = documentRepository.save(document);
		}

		DocumentSectionsRecord existingRecord = sql.fetchOne(DOCUMENT_SECTIONS,
				DOCUMENT_SECTIONS.ID.eq(documentSection.getSectionId()));

		if (existingRecord == null) {
			DocumentSectionsRecord newRecord = sql.newRecord(DOCUMENT_SECTIONS);
			newRecord.setSectionversion(0L);

			newRecord.setDocumentid(document.getId());
			newRecord.setJsondocument(documentSection.asJSON());
			newRecord.setPosition(documentSection.getSectionPosition());
			newRecord.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));

			newRecord.store();

			documentSection.setSectionId(newRecord.getId());

		} else {
			existingRecord
					.setSectionversion(existingRecord.getSectionversion() + 1);

			existingRecord.setDocumentid(document.getId());
			existingRecord.setJsondocument(documentSection.asJSON());
			existingRecord.setPosition(documentSection.getSectionPosition());
			existingRecord
					.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			existingRecord.setLastmodified(Timestamp.valueOf(LocalDateTime
					.now()));

			existingRecord.store();

		}

		logger.info("Saving document section " + documentSection);

		return documentSection;
	}

	public void delete(DocumentSection documentSection) {
		this.delete(documentSection.getSectionId());
	}

	public void delete(Long sectionId) {
		DocumentSectionsRecord existingRecord = sql.fetchOne(DOCUMENT_SECTIONS,
				DOCUMENT_SECTIONS.ID.eq(sectionId));
		existingRecord.delete();
	}

	// /**
	// * Saves a section of a document
	// *
	// * @param documentId
	// * @param body
	// * @return
	// * @throws JsonProcessingException
	// */
	// public void insertDocumentSection(Long documentId, DocumentSection body)
	// throws JsonProcessingException {
	// int execute;
	// execute = sql
	// .insertInto(DOCUMENT_SECTIONS,
	// DOCUMENT_SECTIONS.ID, //
	// DOCUMENT_SECTIONS.SECTIONVERSION,
	// DOCUMENT_SECTIONS.JSONDOCUMENT, //
	// DOCUMENT_SECTIONS.DOCUMENTID, //
	// DOCUMENT_SECTIONS.DATECREATED, //
	// DOCUMENT_SECTIONS.LASTMODIFIED, //
	// DOCUMENT_SECTIONS.POSITION).values(body.getSectionId(), //
	// 1L, body.asJSON(), //
	// documentId, //
	// Timestamp.valueOf(LocalDateTime.now()), //
	// Timestamp.valueOf(LocalDateTime.now()), //
	// body.getSectionPosition()).execute();
	//
	// logger.info("Inserted " + execute + " records.");
	// }
	//
	// public void updateDocumentSection(DocumentSection body)
	// throws JsonProcessingException {
	// sql.update(DOCUMENT_SECTIONS)
	// .set(DOCUMENT_SECTIONS.JSONDOCUMENT, body.asJSON())
	// .set(DOCUMENT_SECTIONS.LASTMODIFIED,
	// Timestamp.valueOf(LocalDateTime.now()))
	// .where(DOCUMENT_SECTIONS.ID.equal(body.getSectionId()))
	// .execute();
	// }

	// public String getSection(Document document, DocumentSection
	// documentSection)
	// throws DocumentSectionNotFoundException {
	//
	// Long sectionId = documentSection.getSectionId();
	// Long documentId = document.getId();
	//
	// Result<Record> records = sql
	// .select()
	// .from(DOCUMENT_SECTIONS)
	// .where(DOCUMENT_SECTIONS.ID.equal(sectionId).and(
	// DOCUMENT_SECTIONS.DOCUMENTID.equal(documentId)))
	// .fetch();
	//
	// logger.info("Records found: " + records.size());
	//
	// String value;
	//
	// try {
	// value = records.get(0).getValue(DOCUMENT_SECTIONS.JSONDOCUMENT);
	//
	// } catch (IndexOutOfBoundsException e) {
	// throw new DocumentSectionNotFoundException("Section " + sectionId
	// + " from document " + documentId + " not found.");
	// }
	//
	// return value;
	// }

}
