package editor.services;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.model.DocumentSection;
import editor.repository.DocumentRepository;
import editor.repository.DocumentSectionNotFoundException;
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
	private DocumentSectionRepository documentSectionRepository;

	/**
	 * Saves the DocumentSection, if the record is already present it will
	 * update instead.
	 * 
	 * @param body
	 * @throws JsonProcessingException
	 */
	public void saveDocumentSection(Long documentId, DocumentSection body)
			throws JsonProcessingException {

		// TODO: replace id with document?
		List<String> documentSections = documentSectionRepository
				.getDocumentSections(documentId);

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
	 * Retrieves a single DocumentSection related to a Document.
	 * 
	 * @param id
	 * @param sectionId
	 * @return DocumentSection
	 * @throws DocumentSectionNotFoundException
	 */
	public DocumentSection getDocumentSection(Long documentId, Long sectionId)
			throws DocumentSectionNotFoundException {

		String json = documentSectionRepository.getSection(documentId,
				sectionId);

		return DocumentSection.fromJSON(json);
	}

	/**
	 * Retrieves all the sections related to a Document.
	 * 
	 * @param documentId
	 * @return
	 */
	public List<DocumentSection> getDocumentSections(Long documentId) {
		return documentSectionRepository.getDocumentSections(documentId)
				.stream().map(DocumentSection::fromJSON)
				.collect(Collectors.toList());
	}

}
