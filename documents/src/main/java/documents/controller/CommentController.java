package documents.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.web.AccountContact;

import com.newrelic.api.agent.NewRelic;

import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.document.sections.DocumentSection;
import documents.model.notifications.CommentNotification;
import documents.repository.DocumentAccessRepository;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.aspect.CheckReadAccess;
import documents.services.notification.NotificationService;

@Controller
@RequestMapping("resume")
public class CommentController {

	private final Logger logger = Logger.getLogger(CommentController.class
			.getName());

	private final DocumentService documentService;

	private final AccountService accountService;

	private final NotificationService notificationService;

	private final DocumentAccessRepository documentAccessRepository;

	@Inject
	public CommentController(DocumentService documentService,
			AccountService accountService,
			NotificationService notificationService,
			DocumentAccessRepository documentAccessRepository) {
		this.documentService = documentService;
		this.accountService = accountService;
		this.notificationService = notificationService;
		this.documentAccessRepository = documentAccessRepository;
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Comment> getCommentsForSection(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			final @AuthenticationPrincipal User user)
			throws ElementNotFoundException {
		final Document document = this.documentService.getDocument(documentId);

		List<Comment> commentsForSection = documentService
				.getCommentsForSection(request, sectionId);

		commentsForSection.forEach(c -> c.setCanDeleteComment(canDeleteComment(
				c.getCommentId(), c, user, document)));

		return commentsForSection;
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public @ResponseBody Comment saveCommentsForSection(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@RequestBody final Comment newComment,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		Optional<DocumentAccess> documentAccess = documentAccessRepository.get(
				user.getUserId(), documentId);

		if (documentAccess.isPresent()
				&& !documentAccess.get().getAllowGuestComments()
				&& !user.isVyllageAdmin())
			throw new AccessDeniedException(
					"You are not allowed to comment on this document.");

		// notification
		Document document = null;
		DocumentSection documentSection = null;

		try {
			document = documentService.getDocument(documentId);
			documentSection = documentService.getDocumentSection(sectionId);

		} catch (ElementNotFoundException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		setCommentData(sectionId, newComment, user);

		final Comment savedComment = documentService.saveComment(request,
				newComment);

		notifyOfNewComment(request, user, document, documentSection,
				savedComment);

		return savedComment;
	}

	protected void notifyOfNewComment(HttpServletRequest request, User user,
			Document document, DocumentSection documentSection,
			final Comment savedComment) {

		notificationService.save(new CommentNotification(document.getUserId(),
				savedComment, documentSection.getTitle()));

		boolean isOwner = document.getUserId().equals(user.getUserId());

		// Check user ids before going to DB...
		// don't notify if the user commenting is the owner of the document...
		if (!isOwner
				&& notificationService.needsToSendEmailNotification(document
						.getUserId())) {

			List<AccountContact> recipient = accountService
					.getContactDataForUsers(request,
							Arrays.asList(document.getUserId()));

			// if we have not, send it
			if (recipient != null && !recipient.isEmpty())
				notificationService.sendEmailNewCommentNotification(user,
						recipient.get(0), savedComment);
		}
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment/{commentId}", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@CheckReadAccess
	public void deleteCommentsForSection(HttpServletRequest request,
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long commentId,
			@RequestBody final Comment comment,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		Document document = this.documentService.getDocument(documentId);

		if (canDeleteComment(commentId, comment, user, document))
			documentService.deleteComment(comment);
	}

	protected boolean canDeleteComment(final Long commentId,
			final Comment comment, final User user, final Document document) {

		// allow the document owner to delete all comments
		if (user.getUserId().equals(document.getUserId()))
			return true;

		// allow the user to delete his own comments
		return user.getUserId().equals(comment.getUserId())
				&& commentId.equals(comment.getCommentId());
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment/{commentId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void saveCommentsForComment(HttpServletRequest request,
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long commentId,
			@RequestBody final Comment comment,
			@AuthenticationPrincipal User user) {

		Optional<DocumentAccess> documentAccess = documentAccessRepository.get(
				user.getUserId(), documentId);

		if (documentAccess.isPresent()
				&& !documentAccess.get().getAllowGuestComments()
				&& !user.isVyllageAdmin())
			throw new AccessDeniedException(
					"You are not allowed to comment on this document.");

		setCommentData(sectionId, comment, user);

		if (comment.getOtherCommentId() == null)
			comment.setOtherCommentId(commentId);

		documentService.saveComment(request, comment);
	}

	protected void setCommentData(@NonNull final Long sectionId,
			@NonNull final Comment comment, @NonNull User user) {
		if (comment.getUserId() == null)
			comment.setUserId(user.getUserId());

		if (comment.getSectionId() == null)
			comment.setSectionId(sectionId);

		if (comment.getUserName() == null || comment.getUserName().isEmpty())
			comment.setUserName(user.getFirstName() + " " + user.getLastName());
	}
}
