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

import editor.domain.editor.tables.DocumentSections;
import editor.domain.editor.tables.records.DocumentSectionsRecord;
import editor.model.Document;
import editor.model.DocumentSection;

@Repository
public class DocumentSectionRepository implements IRepository<DocumentSection> {

	private final Logger logger = Logger
			.getLogger(DocumentSectionRepository.class.getName());

	@Autowired
	private DSLContext sql;

	@Autowired
	private IRepository<Document> documentRepository;

	@Override
	public DocumentSection get(Long id) throws ElementNotFoundException {
		// Result<DocumentSectionsRecord> existingRecords = sql.fetch(
		// DOCUMENT_SECTIONS, DOCUMENT_SECTIONS.ID.eq(id));

		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");

		List<String> existingRecords = sql.select(s1.fields()) //
				.from(s1) //
				.join(s2) //
				.on(s1.ID.eq(s2.ID)) //
				.and(s1.SECTIONVERSION.lessOrEqual(s2.SECTIONVERSION)) //
				.where(s1.ID.eq(id)).fetch(DOCUMENT_SECTIONS.JSONDOCUMENT);

		// select d1.*
		// from docs d1
		// left outer join docs d2
		// on (d1.id = d2.id and d1.rev < d2.rev)
		// where d2.id is null
		// order by id;

		if (existingRecords == null || existingRecords.isEmpty())
			throw new ElementNotFoundException("DocumentSection with id '" + id
					+ "' not found.");
		return DocumentSection.fromJSON(existingRecords.get(0));
	}

	@Override
	public List<DocumentSection> getAll() {
		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");

		List<String> existingRecords = sql.select(s1.fields()) //
				.from(s1) //
				.join(s2) //
				.on(s1.ID.eq(s2.ID)) //
				.and(s1.SECTIONVERSION.lessOrEqual(s2.SECTIONVERSION)) //
				.fetch(DOCUMENT_SECTIONS.JSONDOCUMENT);

		return existingRecords.stream().map(DocumentSection::fromJSON)
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @param documentId
	 *            the id of the related document
	 * @return
	 * @throws DocumentSectionNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Long documentId)
			throws ElementNotFoundException {

		Result<Record> documentSections = sql.select().from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.DOCUMENTID.eq(documentId)).fetch();

		if (documentSections == null)
			throw new ElementNotFoundException(
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
			throws ElementNotFoundException {

		return this.getDocumentSections(document.getId());
	}

	@Override
	public DocumentSection save(DocumentSection documentSection) {

		DocumentSectionsRecord existingRecord = sql.fetchOne(DOCUMENT_SECTIONS,
				DOCUMENT_SECTIONS.ID.eq(documentSection.getSectionId()));

		if (existingRecord == null) {
			logger.info("Saving documentSection: " + documentSection);

			DocumentSectionsRecord newRecord = sql.newRecord(DOCUMENT_SECTIONS);
			newRecord.setSectionversion(0L);
			newRecord.setDocumentid(documentSection.getDocumentId());

			try {
				newRecord.setJsondocument(documentSection.asJSON());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			newRecord.setPosition(documentSection.getSectionPosition());
			newRecord.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));

			newRecord.store();

			documentSection.setSectionId(newRecord.getId());

		} else {

			logger.info(String
					.format("Updating existing document section '%1$s' version '%2$s' to version '%3$s' ",
							existingRecord.getId(),
							existingRecord.getSectionversion(),
							existingRecord.getSectionversion() + 1));

			existingRecord
					.setSectionversion(existingRecord.getSectionversion() + 1);

			try {
				existingRecord.setJsondocument(documentSection.asJSON());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			existingRecord.setPosition(documentSection.getSectionPosition());
			existingRecord
					.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			existingRecord.setLastmodified(Timestamp.valueOf(LocalDateTime
					.now()));

			existingRecord.update();

		}

		logger.info("Saved document section " + documentSection);
		return documentSection;
	}

	@Override
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
