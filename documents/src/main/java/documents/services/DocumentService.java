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
import documents.utilities.FindDuplicates;

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

		logger.info(documentSection.toString());
		DocumentSection savedSection = null;

		// if a section position's has not been set by the client we set the
		// section as the first one.
		if (documentSection.getSectionPosition() == null) {
			documentSection.setSectionPosition(1L);
			try {
				List<DocumentSection> documentSections = documentSectionRepository
						.getDocumentSections(documentSection.getDocumentId());

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

			} catch (ElementNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			savedSection = documentSectionRepository.save(documentSection);
		}

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

	/**
	 * Orders sections according to their position in the list.
	 * 
	 * @param documentId
	 * @param documentSectionIds
	 */
	public void orderDocumentSections(Long documentId,
			List<Long> documentSectionIds) {

		if (documentSectionIds == null || documentSectionIds.isEmpty())
			throw new IllegalArgumentException(
					"The amount of section ids does not match the number of existing sections in the database.");

		// finding duplicates
		FindDuplicates finder = new FindDuplicates();
		if (!finder.findDuplicates(documentSectionIds).isEmpty()) {
			throw new IllegalArgumentException("Duplicate IDs found.");
		}

		try {
			List<DocumentSection> documentSections = documentSectionRepository
					.getDocumentSections(documentId);

			if (documentSectionIds.size() != documentSections.size())
				throw new IllegalArgumentException(
						"The amount of section ids does not match the number of existing sections in the database.");

			if (!documentSections.stream().map(ds -> ds.getSectionId())
					.collect(Collectors.toList())
					.containsAll(documentSectionIds))
				throw new IllegalArgumentException(
						"The sections ids do not match the existing sections in the database.");

			documentSections.stream().forEachOrdered(
					s -> logger.info("Section " + s.getSectionId()
							+ " Position: " + s.getSectionPosition()));

			// set position according to the position of the id in the array.
			// +1 because it starts at 0.
			documentSections.stream().forEach(
					ds -> ds.setSectionPosition((long) documentSectionIds
							.indexOf(ds.getSectionId()) + 1));

			logger.info("--------");
			documentSections.stream().forEachOrdered(
					s -> logger.info("Section " + s.getSectionId()
							+ " Position: " + s.getSectionPosition()));

		} catch (ElementNotFoundException e) {
			e.printStackTrace();
		}
	}

}
