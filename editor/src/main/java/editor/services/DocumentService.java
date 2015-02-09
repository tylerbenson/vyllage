package editor.services;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import editor.model.DocumentSection;
import editor.repository.DocumentRepository;
import editor.repository.DocumentSectionRepository;

/**
 * This service takes care of saving, retrieving and manipulating documents.
 * 
 * @author uh
 *
 */
@Service
public class DocumentService {

	private final Logger logger = Logger.getLogger(DocumentService.class
			.getName());

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	DocumentSectionRepository documentSectionRepository;

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
		Result<Record> documentSections = documentSectionRepository
				.getDocumentSections(documentId, body);

		logger.info("Saving document: " + body.getSectionId());

		// TODO: handle version instead of updating the same document, link to
		// an actual document, obtain sort order from somewhere, etc.
		// Refactor to save a list of sections?

		if (documentSections.isEmpty()) {
			// TODO: get account id

			documentRepository.insertDocument(documentId);

			documentSectionRepository.insertDocumentSection(documentId, body);
		} else {
			logger.info("Records found: " + documentSections.size());

			documentSectionRepository.updateDocumentSection(body);

		}

	}

	/**
	 * @param id
	 * @param sectionId
	 * @return DocumentSection
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public DocumentSection getDocumentSection(Long documentId, Long sectionId)
			throws JsonParseException, JsonMappingException, IOException {

		// TODO: handle exceptions, not found, etc

		String json = documentSectionRepository.getSection(documentId,
				sectionId);

		return DocumentSection.fromJSON(json);
	}

	public List<DocumentSection> getDocumentSections(Long documentId) {
		return documentSectionRepository.getSections(documentId);
	}

}
