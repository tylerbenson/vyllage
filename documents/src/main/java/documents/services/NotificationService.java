package documents.services;

import java.util.Optional;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import user.common.User;

import com.newrelic.api.agent.NewRelic;

import documents.model.AccountContact;
import documents.model.Comment;
import documents.model.UserNotification;
import documents.repository.UserNotificationRepository;
import email.EmailBuilder;

@Service
public class NotificationService {

	private final Logger logger = Logger.getLogger(NotificationService.class
			.getName());

	private static final String EMAIL_RESUME_COMMENT_NOTIFICATION = "email-resume-comment-notification";

	@Autowired
	private Environment environment;

	@Autowired
	private UserNotificationRepository userNotificationRepository;

	@Autowired
	@Qualifier(value = "documents.emailBuilder")
	private EmailBuilder emailBuilder;

	private String SUBJECT = "Vyllage notification";

	/**
	 * Retrieves a single user notification.
	 * 
	 * @param userId
	 * @return
	 */
	public Optional<UserNotification> getNotification(Long userId) {
		return userNotificationRepository.get(userId);
	}

	/**
	 * Notifies a user that someone has commented his resume.
	 * 
	 * @param user
	 *            the user originating the notification
	 * @param accountContact
	 *            the user we are notifying
	 * @param comment
	 *            the comment
	 * 
	 */
	public void sendEmailNewCommentNotification(User user,
			AccountContact accountContact, Comment comment) {

		try {
			logger.info("Sending notification email.");
			emailBuilder
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
					.setNoHtmlMessage("A user has commented your resume.")
					.send();

			userNotificationRepository.save(new UserNotification(accountContact
					.getUserId()));

		} catch (EmailException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

	}
}
