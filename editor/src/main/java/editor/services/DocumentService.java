package editor.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import editor.model.Document;
import editor.model.DocumentSection;
import editor.repository.DocumentRepository;
import editor.repository.DocumentSectionRepository;
import editor.repository.ElementNotFoundException;

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

	public Document saveDocument(Document document) {
		logger.info("Saving document " + document);
		return documentRepository.save(document);
	}

	/**
	 * Saves the DocumentSection, if the record is already present it will
	 * update instead.
	 * 
	 * @param body
	 * @return the saved document
	 * @throws
	 */
	public DocumentSection saveDocumentSection(Document document,
			DocumentSection documentSection) {

		logger.info("Saving document section: "
				+ documentSection.getSectionId() + " from document "
				+ document.getId());

		try {
			documentRepository.get(document.getId());

		} catch (ElementNotFoundException e) {
			logger.info("Document with id" + document.getId()
					+ "not found, saving document first.");
			document = this.saveDocument(document);
		}

		documentSection.setDocumentId(document.getId());

		return documentSectionRepository.save(documentSection);

	}

	/**
	 * Retrieves a single DocumentSection.
	 * 
	 * @param id
	 * @param sectionId
	 * @return DocumentSection
	 * @throws ElementNotFoundException
	 */
	public DocumentSection getDocumentSection(Long sectionId)
			throws ElementNotFoundException {
		return documentSectionRepository.get(sectionId);
	}

	/**
	 * Retrieves all the sections related to a Document.
	 * 
	 * @param documentId
	 * @return
	 * @throws ElementNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Document document)
			throws ElementNotFoundException {
		return getDocumentSections(document.getId());
	}

	/**
	 * Retrieves all the sections related to a Document.
	 * 
	 * @param documentId
	 * @return
	 * @throws ElementNotFoundException
	 */
	public List<DocumentSection> getDocumentSections(Long documentId)
			throws ElementNotFoundException {
		return documentSectionRepository.getDocumentSections(documentId);
	}

	public Document getDocument(Long documentId)
			throws ElementNotFoundException {
		return documentRepository.get(documentId);
	}

	public void deleteSection(Long sectionId) {
		documentSectionRepository.delete(sectionId);
	}
	
	public Document getDocumentByUser(Long userId)
			throws ElementNotFoundException {
		return documentRepository.getDocumentByUser(userId);
	}

}
