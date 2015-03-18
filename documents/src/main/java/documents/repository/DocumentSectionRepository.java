package documents.repository;

import static documents.domain.tables.DocumentSections.DOCUMENT_SECTIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import documents.domain.tables.DocumentSections;
import documents.domain.tables.records.DocumentSectionsRecord;
import documents.model.Document;
import documents.model.DocumentSection;

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

		Result<Record3<String, Long, Long>> existingRecords = sql
				.select(s1.JSONDOCUMENT, s1.DOCUMENTID, s1.SECTIONVERSION) //
				.from(s1) //
				.leftOuterJoin(s2) //
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION))) //
				.where(s2.ID.isNull().and(s1.ID.eq(id))) //
				.orderBy(s1.SECTIONVERSION).fetch();

		// select d1.*
		// from docs d1
		// left outer join docs d2
		// on (d1.id = d2.id and d1.rev < d2.rev)
		// where d2.id is null
		// order by id;

		if (existingRecords == null || existingRecords.isEmpty())
			throw new ElementNotFoundException("DocumentSection with id '" + id
					+ "' not found.");
		return generateDocumentSection(existingRecords.get(0));
	}

	@Override
	public List<DocumentSection> getAll() {
		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");

		Result<Record3<String, Long, Long>> existingRecords = sql
				.select(s1.JSONDOCUMENT, s1.DOCUMENTID, s1.SECTIONVERSION) //
				.from(s1) //
				.leftOuterJoin(s2) //
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION))) //
				.where(s2.ID.isNull()).fetch();

		return existingRecords.stream()
				.map(DocumentSectionRepository::generateDocumentSection)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all the sections of the document
	 *
	 * @param documentId
	 *            the id of the related document
	 * @return
	 * @throws DocumentSectionNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Long documentId)
			throws ElementNotFoundException {

		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");

		Result<Record3<String, Long, Long>> existingRecords = sql
				.select(s1.JSONDOCUMENT, s1.DOCUMENTID, s1.SECTIONVERSION)
				.from(s1)
				.leftOuterJoin(s2)
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION)))
				.where(s2.ID.isNull().and(s1.DOCUMENTID.eq(documentId)))
				.fetch();

		if (existingRecords == null)
			throw new ElementNotFoundException(
					"DocumentSections for Document id '" + documentId
							+ "' not found.");

		return existingRecords.stream()
				.map(DocumentSectionRepository::generateDocumentSection)
				.collect(Collectors.toList());
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

	// TODO: check for concurrency problems!
	@Override
	public DocumentSection save(DocumentSection documentSection) {

		DocumentSections s1 = DOCUMENT_SECTIONS.as("s1");
		DocumentSections s2 = DOCUMENT_SECTIONS.as("s2");

		List<DocumentSectionsRecord> records = sql
				.select()
				.from(s1)
				.leftOuterJoin(s2)
				.on(s1.ID.eq(s2.ID).and(
						s1.SECTIONVERSION.lessThan(s2.SECTIONVERSION)))
				.where(s2.ID.isNull().and(
						s1.ID.eq(documentSection.getSectionId())))
				.orderBy(s1.SECTIONVERSION)
				.fetchInto(DocumentSectionsRecord.class);

		DocumentSectionsRecord existingRecord = records != null
				&& !records.isEmpty() ? records.get(0) : null;

		if (existingRecord == null) {

			DocumentSectionsRecord newRecord = sql.newRecord(DOCUMENT_SECTIONS);
			long newSectionVersion = 0L;

			newRecord.setSectionversion(newSectionVersion);
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
			documentSection.setSectionVersion(newSectionVersion);

		} else {

			long nextVersion = existingRecord.getSectionversion() + 1;

			logger.info(String
					.format("Updating existing document section '%1$s' version '%2$s' to version '%3$s' ",
							existingRecord.getId(),
							existingRecord.getSectionversion(), nextVersion));

			existingRecord.setSectionversion(nextVersion);

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

			existingRecord.store();
			documentSection.setSectionVersion(nextVersion);

		}

		return documentSection;
	}

	@Override
	public void delete(Long sectionId) {
		Result<DocumentSectionsRecord> existingRecords = sql.fetch(
				DOCUMENT_SECTIONS, DOCUMENT_SECTIONS.ID.eq(sectionId));
		existingRecords.forEach(r -> r.delete());
	}

	public boolean exists(Long sectionId) {
		return sql.fetchExists(sql.select().from(DOCUMENT_SECTIONS)
				.where(DOCUMENT_SECTIONS.ID.eq(sectionId)));
	}

	/**
	 * Generates a DocumentSection from the records containing the latest
	 * versions of the document sections.
	 * 
	 * @param existingRecord
	 * @return
	 */
	private static DocumentSection generateDocumentSection(
			Record3<String, Long, Long> existingRecord) {
		DocumentSection fromJSON = DocumentSection.fromJSON(existingRecord
				.value1());
		fromJSON.setDocumentId(existingRecord.value2());
		fromJSON.setSectionVersion(existingRecord.value3());
		return fromJSON;
	}

}
