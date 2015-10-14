package documents.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import user.common.User;
import user.common.web.AccountContact;

import com.newrelic.api.agent.NewRelic;

import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.DocumentHeader;
import documents.model.SectionAdvice;
import documents.model.constants.DocumentAccessEnum;
import documents.model.constants.DocumentTypeEnum;
import documents.model.document.sections.DocumentSection;
import documents.repository.CommentRepository;
import documents.repository.DocumentAccessRepository;
import documents.repository.DocumentRepository;
import documents.repository.DocumentSectionRepository;
import documents.repository.ElementNotFoundException;
import documents.repository.SectionAdviceRepository;
import documents.services.rules.MergeOnSectionDuplicate;
import documents.services.rules.OrderSectionValidator;
import documents.services.rules.SetSectionPositionOnCreationUpdate;

/**
 * This service takes care of saving, retrieving and manipulating documents.
 *
 * @author uh
 */
@Service
public class DocumentService {

	private final Logger logger = Logger.getLogger(DocumentService.class
			.getName());

	private final DocumentRepository documentRepository;

	private final DocumentSectionRepository documentSectionRepository;

	private final CommentRepository commentRepository;

	private final SectionAdviceRepository sectionAdviceRepository;

	private final AccountService accountService;

	private final OrderSectionValidator orderSectionValidator;

	private final DocumentAccessRepository documentAccessRepository;

	private final SetSectionPositionOnCreationUpdate setSectionPositionOnCreationUpdate;

	private final MergeOnSectionDuplicate mergeOnSectionDuplicate;

	@Inject
	public DocumentService(DocumentRepository documentRepository,
			DocumentSectionRepository documentSectionRepository,
			CommentRepository commentRepository,
			SectionAdviceRepository suggestionRepository,
			AccountService accountService,
			OrderSectionValidator orderSectionValidator,
			DocumentAccessRepository documentAccessRepository) {
		super();
		this.documentRepository = documentRepository;
		this.documentSectionRepository = documentSectionRepository;
		this.commentRepository = commentRepository;
		this.sectionAdviceRepository = suggestionRepository;
		this.accountService = accountService;
		this.orderSectionValidator = orderSectionValidator;
		this.documentAccessRepository = documentAccessRepository;

		this.setSectionPositionOnCreationUpdate = new SetSectionPositionOnCreationUpdate();
		this.mergeOnSectionDuplicate = new MergeOnSectionDuplicate();
	}

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
	public DocumentSection saveDocumentSection(
			@NonNull final DocumentSection documentSection) {

		logger.info(documentSection.toString());
		DocumentSection savedSection = null;

		try {
			final List<DocumentSection> documentSections = this.documentSectionRepository
					.getDocumentSections(documentSection.getDocumentId());

			// merge duplicate sections
			final List<DocumentSection> toDelete = this.mergeOnSectionDuplicate
					.apply(documentSection, documentSections);

			if (toDelete != null && !toDelete.isEmpty()) {
				toDelete.stream().forEach(
						s -> this.documentSectionRepository.delete(s
								.getSectionId()));

				// removing deleted sections
				documentSections.removeAll(toDelete);

			}

			// set section positions
			final List<DocumentSection> sectionsToUpdate = this.setSectionPositionOnCreationUpdate
					.apply(documentSection, documentSections);

			// update their positions
			if (sectionsToUpdate != null && !sectionsToUpdate.isEmpty())
				sectionsToUpdate.stream().forEach(
						s -> this.documentSectionRepository.save(s));

			savedSection = this.documentSectionRepository.save(documentSection);

			return savedSection;

		} catch (ElementNotFoundException e) {
			// there are none, therefore it's the first one
			long first = 1L;
			documentSection.setSectionPosition(first);
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
	 * Returns the user's document, if it can't find one creates one with no
	 * sections.
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
			newDocument.setDocumentType(DocumentTypeEnum.RESUME.name());
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

		List<AccountContact> names = accountService.getContactDataForUsers(
				request, comments.stream() //
						// filter deleted comments
						.filter(c -> !c.isDeleted()) //
						.map(c -> c.getUserId()) //
						.collect(Collectors.toList()));

		for (Comment comment : comments) {
			if (!comment.isDeleted()) {
				this.addContactDataToComment(names, comment);
			}
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

	public Comment saveComment(final HttpServletRequest request,
			final Comment comment) {
		List<AccountContact> names = accountService.getContactDataForUsers(
				request, Arrays.asList(comment.getUserId()));

		final Comment savedComment = commentRepository.save(comment);

		if (names != null && !names.isEmpty()) {
			this.addContactDataToComment(names, savedComment);
		}

		return savedComment;
	}

	/**
	 * Adds contact information about the user who created the comment.
	 * 
	 * @param namessaved
	 * @param comment
	 */
	protected void addContactDataToComment(List<AccountContact> names,
			final Comment comment) {
		Optional<AccountContact> accountContact = names.stream()
				.filter(an -> an.getUserId().equals(comment.getUserId()))
				.findFirst();

		accountContact.ifPresent(an -> comment.setUserName(an.getFirstName()
				+ " " + an.getLastName()));

		accountContact.ifPresent(an -> comment.setAvatarUrl(an.getAvatarUrl()));
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
	 * @return true | false
	 */
	public boolean existsForUser(User user, Long documentId) {
		return documentRepository.existsForUser(user, documentId);
	}

	public boolean exists(Long documentId) {
		return documentRepository.exists(documentId);
	}

	public List<Document> getDocumentByUserAndType(Long userId,
			DocumentTypeEnum documentTypeEnum) {
		return documentRepository.getDocumentByUserAndType(userId,
				documentTypeEnum);
	}

	/**
	 * Checks that a given user can read or write a given document.
	 *
	 * @param userId
	 * @param documentId
	 * @param access
	 * @return true | false
	 */
	public boolean checkAccess(Long userId, Long documentId,
			DocumentAccessEnum access) {
		Optional<DocumentAccess> documentAccess = documentAccessRepository.get(
				userId, documentId);

		if (documentAccess.isPresent())
			return documentAccess.get().checkAccess(access);
		return false;
	}

	/**
	 * Returns all the permissions for a given document.
	 *
	 * @param documentId
	 * @return
	 */
	public List<DocumentAccess> getDocumentPermissions(Long documentId) {
		return documentAccessRepository.get(documentId);
	}

	/**
	 * Returns all the user's document access permissions he granted.
	 *
	 * @param user
	 * @return
	 */
	public List<DocumentAccess> getUserDocumentsPermissions(User user) {
		return documentAccessRepository.getFromUserDocuments(user);
	}

	/**
	 * Revokes a documentAccess, deleting it.
	 *
	 * @param documentAccess
	 */
	public void deleteDocumentAccess(DocumentAccess documentAccess) {
		documentAccessRepository.delete(documentAccess);
	}

	public Map<Long, String> getTaglines(List<Long> userIds) {
		return documentRepository.getTaglines(userIds);
	}

	public void setUserDocumentsPermissions(DocumentAccess documentAccess) {
		documentAccessRepository.create(documentAccess);
	}

	public DocumentHeader getDocumentHeader(HttpServletRequest request,
			final Long documentId, User user) throws ElementNotFoundException {
		Document document = this.getDocument(documentId);

		DocumentHeader header = new DocumentHeader();

		if (document.getUserId().equals(user.getUserId())) {
			header.setOwner(true);
		}

		List<AccountContact> accountContactData = accountService
				.getContactDataForUsers(request,
						Arrays.asList(document.getUserId()));

		if (accountContactData != null && !accountContactData.isEmpty()) {
			header.setFirstName(accountContactData.get(0).getFirstName());
			header.setMiddleName(accountContactData.get(0).getMiddleName());
			header.setLastName(accountContactData.get(0).getLastName());

			header.setAddress(accountContactData.get(0).getAddress());
			header.setEmail(accountContactData.get(0).getEmail());
			header.setPhoneNumber(accountContactData.get(0).getPhoneNumber());
			header.setTwitter(accountContactData.get(0).getTwitter());
			header.setLinkedIn(accountContactData.get(0).getLinkedIn());
			header.setAvatarUrl(accountContactData.get(0).getAvatarUrl());
			header.setSiteUrl(accountContactData.get(0).getSiteUrl());
		}

		header.setTagline(document.getTagline());
		return header;
	}

	public List<SectionAdvice> getSectionAdvices(HttpServletRequest request,
			Long sectionId) {

		List<SectionAdvice> sectionAdvices = sectionAdviceRepository
				.getSectionAdvices(sectionId);

		if (sectionAdvices == null || sectionAdvices.isEmpty())
			return Collections.emptyList();

		List<AccountContact> names = accountService.getContactDataForUsers(
				request, sectionAdvices.stream().map(s -> s.getUserId())
						.collect(Collectors.toList()));

		for (SectionAdvice sectionAdvice : sectionAdvices) {
			this.addContactDataToAdvice(names, sectionAdvice);
		}

		return sectionAdvices;
	}

	public SectionAdvice saveSectionAdvice(HttpServletRequest request,
			final SectionAdvice sectionAdvice) {
		final SectionAdvice savedSection = sectionAdviceRepository
				.save(sectionAdvice);

		List<AccountContact> names = accountService.getContactDataForUsers(
				request, Arrays.asList(savedSection.getUserId()));

		this.addContactDataToAdvice(names, savedSection);

		return savedSection;
	}

	/**
	 * Adds contact information about the user who created the advice.
	 * 
	 * @param sectionAdvice
	 * @param names
	 */
	protected void addContactDataToAdvice(final List<AccountContact> names,
			final SectionAdvice sectionAdvice) {
		Optional<AccountContact> accountContact = names.stream()
				.filter(an -> an.getUserId().equals(sectionAdvice.getUserId()))
				.findFirst();

		accountContact.ifPresent(an -> sectionAdvice.setUserName(an
				.getFirstName() + " " + an.getLastName()));

		accountContact.ifPresent(an -> sectionAdvice.setAvatarUrl(an
				.getAvatarUrl()));
	}

	public void deleteComment(final Comment comment) {

		// check if it's a comment that references another comment
		if (comment.getOtherCommentId() != null) {
			// change comment text and mark as deleted so we won't load the user
			// name later
			comment.markDeleted();
			commentRepository.save(comment);

			return;

			// check if it's referenced by another comment
		} else if (commentRepository.isReferencedByOthers(comment)) {
			comment.markDeleted();
			commentRepository.save(comment);

			return;
		}

		// if not just delete normally.
		commentRepository.delete(comment.getCommentId());
	}

	public Map<Long, Integer> getNumberOfAdvicesForSections(
			List<Long> sectionIds) {
		return sectionAdviceRepository
				.getNumberOfAdvicesForSections(sectionIds);
	}

	public int getNumberOfAdvicesForSection(Long sectionId) {
		return sectionAdviceRepository.getNumberOfAdvicesForSections(
				Arrays.asList(sectionId)).getOrDefault(sectionId, 0);
	}

}
