package documents.model.notifications;

import java.time.LocalDateTime;

import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class WebCommentNotification {

	private final Long userId;
	private final Long commentUserId;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private final LocalDateTime dateCreated;

	private final Long commentId;

	private final String sectionTitle;

	private String userName;

	public WebCommentNotification(CommentNotification commentNotification) {
		this.userId = commentNotification.getUserId();
		this.commentUserId = commentNotification.getCommentUserId();
		this.dateCreated = commentNotification.getDateCreated();
		this.commentId = commentNotification.getCommentId();
		this.sectionTitle = commentNotification.getSectionTitle();
	}

	public Long getUserId() {
		return userId;
	}

	public Long getCommentUserId() {
		return commentUserId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public Long getCommentId() {
		return commentId;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
