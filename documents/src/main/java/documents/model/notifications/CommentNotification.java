package documents.model.notifications;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.NonNull;
import documents.model.Comment;

/**
 * Used to determine if a user has received a notification about someone
 * commenting on his resume.
 *
 * @author uh
 */
public class CommentNotification {
	private final Long userId;
	private LocalDateTime dateCreated;
	private final Long commentId;

	public CommentNotification(@NonNull Long userId, @NonNull Comment comment) {
		this.userId = userId;
		this.commentId = comment.getCommentId();

	}

	public CommentNotification(@NonNull Long userId,
			@NonNull LocalDateTime dateCreated, @NonNull Long commentId) {
		this.userId = userId;
		this.dateCreated = dateCreated;
		this.commentId = commentId;
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

	public boolean wasSentToday() {
		return LocalDateTime.now(ZoneId.of("UTC")).getDayOfMonth() == getDateCreated()
				.atZone(ZoneId.of("UTC")).getDayOfMonth();
	}
}
