package documents.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.common.User;

import com.newrelic.api.agent.NewRelic;

import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentSection;
import documents.repository.CommentRepository;
import documents.repository.DocumentRepository;
import documents.repository.DocumentSectionRepository;
import documents.repository.ElementNotFoundException;
import documents.repository.SuggestionRepository;
import documents.utilities.OrderSectionValidator;

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

	@Autowired
	private OrderSectionValidator orderSectionValidator;

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
	 * @throws ElementNotFoundException
	 */
	public DocumentSection saveDocumentSection(DocumentSection documentSection)
			throws ElementNotFoundException {

		logger.info(documentSection.toString());
		DocumentSection savedSection = null;

		// if a section position has not been set by the client we set the
		// section as the first one.
		documentSection
				.setSectionPosition(documentSection.getSectionPosition() == null
						|| documentSection.getSectionPosition() <= 0 ? 1L
						: documentSection.getSectionPosition());

		try {
			List<DocumentSection> documentSections = documentSectionRepository
					.getDocumentSections(documentSection.getDocumentId());

			// sort if the section does not exist.
			if (documentSections.stream().noneMatch(
					ds -> ds.getSectionId().equals(
							documentSection.getSectionId()))) {

				documentSections.stream().forEachOrdered(
						s -> logger.info("Section " + s.getSectionId() + " P: "
								+ s.getSectionPosition()));

				// sort by position in case they are not sorted already and
				// shift 1

				documentSections
						.stream()
						.sorted((s1, s2) -> s1.getSectionPosition().compareTo(
								s2.getSectionPosition())) //
						.map(s -> {
							s.setSectionPosition(s.getSectionPosition() + 1L);
							return s;
						}).forEach(s -> documentSectionRepository.save(s));

				savedSection = documentSectionRepository.save(documentSection);

				documentSections.stream().forEachOrdered(
						s -> logger.info("Section " + s.getSectionId() + " P: "
								+ s.getSectionPosition()));
				return savedSection;
			}

		} catch (ElementNotFoundException e) {
			// do nothing just save normally
		}

		savedSection = documentSectionRepository.save(documentSection);

		return savedSection;

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
	public List<DocumentSection> getDocumentSections(Long documentId)
			throws ElementNotFoundException {
		return documentSectionRepository.getDocumentSections(documentId);
	}

	public Document getDocument(Long documentId)
			throws ElementNotFoundException {
		return documentRepository.get(documentId);
	}

	public void deleteSection(Long documentId, Long sectionId)
			throws ElementNotFoundException {

		if (!documentSectionRepository.exists(documentId, sectionId)) {
			ElementNotFoundException e = new ElementNotFoundException(
					"Section with id '" + sectionId + "' could not be found.");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		documentSectionRepository.delete(sectionId);
	}

	public boolean sectionExists(DocumentSection documentSection) {
		return documentSectionRepository
				.exists(documentSection.getDocumentId(),
						documentSection.getSectionId());
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

	/**
	 * Orders sections according to their position in the list.
	 * 
	 * @param documentId
	 * @param documentSectionIds
	 * @throws ElementNotFoundException
	 */
	public void orderDocumentSections(Long documentId,
			List<Long> documentSectionIds) throws ElementNotFoundException {

		orderSectionValidator.checkNullOrEmptyParameters(documentId,
				documentSectionIds);

		// finding duplicates
		orderSectionValidator.checkDuplicateSectionIds(documentSectionIds);

		List<DocumentSection> documentSections = new ArrayList<>();

		try {
			documentSections = documentSectionRepository
					.getDocumentSections(documentId);

		} catch (ElementNotFoundException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		orderSectionValidator.compareExistingIdsWithRequestedIds(
				documentSectionIds, documentSections);

		documentSections.stream().forEachOrdered(
				s -> logger.info("Section " + s.getSectionId() + " Position: "
						+ s.getSectionPosition()));

		// set position according to the position of the id in the array.
		// +1 because it starts at 0.
		documentSections.stream().forEach(
				ds -> ds.setSectionPosition((long) documentSectionIds
						.indexOf(ds.getSectionId()) + 1));

		logger.info("--------");
		documentSections.stream().forEachOrdered(
				s -> logger.info("Section " + s.getSectionId() + " Position: "
						+ s.getSectionPosition()));

		documentSections.stream().forEachOrdered(
				s -> documentSectionRepository.save(s));

	}

	/**
	 * Checks if a given document id exists for a given user. A document will
	 * exist if, it exists, has sections and the user owns the document.
	 * 
	 * @param user
	 * @param documentId
	 * @return
	 */
	public boolean existsForUser(User user, Long documentId) {
		return documentRepository.existsForUser(user, documentId);
	}

}
