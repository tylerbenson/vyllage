package editor.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.model.Document;
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
	 * @return the saved document
	 * @throws JsonProcessingException
	 * @throws
	 */
	public DocumentSection saveDocumentSection(Document document,
			DocumentSection body) throws JsonProcessingException {

		logger.info("Saving document section: " + body.getSectionId()
				+ " from document " + document.getId());

		return documentSectionRepository.save(document, body);

	}

	/**
	 * Retrieves a single DocumentSection.
	 * 
	 * @param id
	 * @param sectionId
	 * @return DocumentSection
	 * @throws DocumentSectionNotFoundException
	 */
	public DocumentSection getDocumentSection(Long sectionId)
			throws DocumentSectionNotFoundException {
		return documentSectionRepository.get(sectionId);
	}

	/**
	 * Retrieves all the sections related to a Document.
	 * 
	 * @param documentId
	 * @return
	 * @throws DocumentSectionNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Document document)
			throws DocumentSectionNotFoundException {
		return getDocumentSections(document.getId());
	}

	/**
	 * Retrieves all the sections related to a Document.
	 * 
	 * @param documentId
	 * @return
	 * @throws DocumentSectionNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Long documentId)
			throws DocumentSectionNotFoundException {
		return documentSectionRepository.getDocumentSections(documentId);
	}

	public Document getDocument(Long documentId) {
		return documentRepository.get(documentId);
	}

}
