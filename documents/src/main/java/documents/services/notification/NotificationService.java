package documents.services.notification;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import user.common.User;
import user.common.web.AccountContact;

import com.newrelic.api.agent.NewRelic;

import documents.model.Comment;
import documents.model.notifications.CommentNotification;
import documents.model.notifications.FeedbackRequestNotification;
import documents.repository.CommentNotificationRepository;
import documents.repository.FeedbackRequestNotificationRepository;
import email.EmailBuilder;

@Service
public class NotificationService {

	private final Logger logger = Logger.getLogger(NotificationService.class
			.getName());

	private static final String EMAIL_RESUME_COMMENT_NOTIFICATION = "email-resume-comment-notification";

	private final Environment environment;

	private final CommentNotificationRepository commentNotificationRepository;

	private final FeedbackRequestNotificationRepository feedbackRequestNotificationRepository;

	private final EmailBuilder emailBuilder;

	private final String SUBJECT = "Vyllage notification";

	@Inject
	public NotificationService(
			Environment environment,
			CommentNotificationRepository userNotificationRepository,
			FeedbackRequestNotificationRepository feedbackRequestNotificationRepository,
			@Qualifier(value = "documents.emailBuilder") EmailBuilder emailBuilder) {
		this.environment = environment;
		this.commentNotificationRepository = userNotificationRepository;
		this.feedbackRequestNotificationRepository = feedbackRequestNotificationRepository;
		this.emailBuilder = emailBuilder;

	}

	/**
	 * Saves a notification about a new comment for the given user.
	 * 
	 * @param userId
	 *            the user owning the document
	 * @param comment
	 * @param sectionTitle
	 */
	public void saveCommentNotification(Long userId, Comment comment,
			String sectionTitle) {
		commentNotificationRepository.save(new CommentNotification(userId,
				comment, sectionTitle));
	}

	/**
	 * Verifies if we need to send an email notification to the user.
	 * 
	 * @param userId
	 * @return
	 */
	public boolean needsToSendEmailNotification(Long userId) {
		// check that we have not sent a message today
		List<CommentNotification> notifications = getCommentNotifications(userId);

		boolean present = notifications != null;

		boolean noneWereSentToday = present
				&& notifications.stream().noneMatch(n -> n.wasSentToday());

		return noneWereSentToday;

		// return !notification.isPresent() ||
		// !notification.get().wasSentToday();
	}

	/**
	 * Notifies a user that someone has commented on his resume.
	 *
	 * @param user
	 *            the user originating the notification
	 * @param accountContact
	 *            the user we are notifying
	 * @param comment
	 *            the comment
	 */
	public void sendEmailNewCommentNotification(User user,
			AccountContact accountContact, Comment comment) {

		try {
			logger.info("Sending notification email.");

			generateEmail(user, accountContact, comment).send();

		} catch (EmailException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

	}

	protected EmailBuilder generateEmail(User user,
			AccountContact accountContact, Comment comment) {
		return emailBuilder
				.from(environment.getProperty("email.from",
						"no-reply@vyllage.com"))
				.fromUserName(
						environment.getProperty("email.from.userName",
								"Chief of Vyllage"))
				.to(accountContact.getEmail())
				.subject(SUBJECT)
				.addTemplateVariable("comment", comment.getCommentText())
				.addTemplateVariable("recipientName",
						accountContact.getFirstName())
				.addTemplateVariable("senderName", user.getFirstName())
				.templateName(EMAIL_RESUME_COMMENT_NOTIFICATION)
				.setNoHtmlMessage("A user has commented your resume.");
	}

	/**
	 * Retrieves all comment notifications.
	 *
	 * @param userId
	 * @return
	 */
	public List<CommentNotification> getCommentNotifications(Long userId) {
		return commentNotificationRepository.get(userId);
	}

	public void saveFeedBackRequestNotification(
			FeedbackRequestNotification feedbackRequestNotification) {
		feedbackRequestNotificationRepository.save(feedbackRequestNotification);
	}

	public List<FeedbackRequestNotification> getFeedbackRequestNotifications(
			Long userId) {
		return feedbackRequestNotificationRepository.get(userId);
	}

}
