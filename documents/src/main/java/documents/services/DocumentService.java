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
		return commentRepository.getNumberOfCommentsForSection(sectionId);
	}

	public Map<Long, Integer> getNumberOfCommentsForSections(
			List<Long> sectionIds) {
		return commentRepository.getNumberOfCommentsForSections(sectionIds);
	}

	public void saveComment(Comment comment) {
		commentRepository.save(comment);
	}

}
