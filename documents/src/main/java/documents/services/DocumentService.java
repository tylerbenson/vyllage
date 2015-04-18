package documents.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentSection;
import documents.repository.CommentRepository;
import documents.repository.DocumentRepository;
import documents.repository.DocumentSectionRepository;
import documents.repository.ElementNotFoundException;
import documents.repository.SuggestionRepository;

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

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private SuggestionRepository suggestionRepository;

	@Autowired
	private AccountService accountService;

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
	public DocumentSection saveDocumentSection(DocumentSection documentSection) {

		// logger.info("Saving document section: "
		// + documentSection.getSectionId() + " from document "
		// + document.getDocumentId());
		//
		// try {
		// documentRepository.get(document.getDocumentId());
		//
		// } catch (ElementNotFoundException e) {
		// logger.info("Document with id" + document.getDocumentId()
		// + "not found, saving document first.");
		// document = this.saveDocument(document);
		// }

		// documentSection.setDocumentId(document.getDocumentId());

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
		return getDocumentSections(document.getDocumentId());
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

	/**
	 * Returns the user's document, if it can't find one creates it.
	 * 
	 * @param userId
	 * @return
	 */
	public Document getDocumentByUser(Long userId) {

		Document document = null;
		try {
			document = documentRepository.getDocumentByUser(userId);

		} catch (ElementNotFoundException e) {
			// no document found for this user, creating one...

			Document newDocument = new Document();
			newDocument.setUserId(userId);
			newDocument.setVisibility(true);
			document = documentRepository.save(newDocument);
		}

		return document;
	}

	public List<Long> getRecentUsersForDocument(Long documentId) {
		return documentRepository.getRecentUsersForDocument(documentId);
	}

	public List<Comment> getCommentsForSection(HttpServletRequest request,
			Long sectionId) {
		List<Comment> comments = commentRepository
				.getCommentsForSection(sectionId);

		if (comments == null || comments.isEmpty())
			return Arrays.asList();

		List<AccountNames> names = accountService.getNamesForUsers(comments
				.stream().map(c -> c.getUserId()).collect(Collectors.toList()),
				request);

		for (Comment comment : comments) {
			Optional<AccountNames> accountNames = names.stream()
					.filter(an -> an.getUserId().equals(comment.getUserId()))
					.findFirst();

			accountNames.ifPresent(an -> comment.setUserName(an.getFirstName()
					+ " " + an.getLastName()));
		}

		return comments;
	}

	public int getNumberOfCommentsForSection(Long sectionId) {
		return commentRepository.getNumberOfCommentsForSections(
				Arrays.asList(sectionId)).getOrDefault(sectionId, 0);
	}

	public Map<Long, Integer> getNumberOfCommentsForSections(
			List<Long> sectionIds) {
		return commentRepository.getNumberOfCommentsForSections(sectionIds);
	}

	public Comment saveComment(Comment comment) {
		return commentRepository.save(comment);
	}

	/**
	 * Deletes all documents from a user.
	 * 
	 * @param userId
	 */
	public void deleteDocumentsFromUser(Long userId) {
		documentRepository.deleteForUser(userId);
	}

}
