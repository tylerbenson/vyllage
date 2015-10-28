package documents.model.notifications;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.NonNull;
import documents.domain.tables.records.CommentNotificationRecord;
import documents.model.Comment;

/**
 * Used to determine if a user has received a notification about someone
 * commenting on his resume.
 *
 * @author uh
 */
public class CommentNotification {
	private final Long userId;
	private final Long commentUserId;

	private LocalDateTime dateCreated;
	private final Long commentId;

	private final String sectionTitle;

	public CommentNotification(@NonNull Long userId, @NonNull Comment comment,
			String sectionTitle) {
		this.userId = userId;
		this.commentUserId = comment.getUserId();
		this.commentId = comment.getCommentId();
		this.sectionTitle = sectionTitle;

	}

	public CommentNotification(CommentNotificationRecord record) {
		this.userId = record.getUserId();
		this.commentUserId = record.getCommentUserId();
		this.dateCreated = record.getDateCreated().toLocalDateTime();
		this.commentId = record.getCommentId();
		this.sectionTitle = record.getSectionTitle();
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public Long getCommentId() {
		return commentId;
	}

	public Long getCommentUserId() {
		return commentUserId;
	}

	public boolean wasSentToday() {
		return LocalDateTime.now(ZoneId.of("UTC")).getDayOfMonth() == getDateCreated()
				.atZone(ZoneId.of("UTC")).getDayOfMonth();
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

}
