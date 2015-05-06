package documents.services;

import java.util.Optional;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import user.common.User;
import documents.model.AccountContact;
import documents.model.Comment;
import documents.model.UserNotification;
import documents.repository.UserNotificationRepository;
import email.EmailBuilder;

@Service
public class NotificationService {

	private static final String EMAIL_RESUME_COMMENT_NOTIFICATION = "email-resume-comment-notification";

	@Autowired
	private Environment environment;

	@Autowired
	private UserNotificationRepository userNotificationRepository;

	@Autowired
	@Qualifier(value = "documents.emailBuilder")
	private EmailBuilder emailBuilder;

	private String SUBJECT = "Vyllage notification";

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
	public void sendEmailNewCommentNotification(User user, AccountContact accountContact,
			Comment comment) {

		try {
			emailBuilder
					.from(environment.getProperty("email.from", "no-reply@vyllage.com"))
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

		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
