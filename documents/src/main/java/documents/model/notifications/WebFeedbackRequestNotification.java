package documents.model.notifications;

import java.time.LocalDateTime;

import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class WebFeedbackRequestNotification {
	private Long userId;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime dateCreated;

	private String userName;

	private Long resumeId;

	private Long resumeUserId;

	public WebFeedbackRequestNotification() {
	}

	public WebFeedbackRequestNotification(
			FeedbackRequestNotification feedbackRequestNotification) {
		this.userId = feedbackRequestNotification.getUserId();
		this.dateCreated = feedbackRequestNotification.getDateCreated();
		this.resumeId = feedbackRequestNotification.getResumeId();
		this.resumeUserId = feedbackRequestNotification.getResumeUserId();
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public String getUserName() {
		return userName;
	}

	public Long getResumeId() {
		return resumeId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getResumeUserId() {
		return resumeUserId;
	}
}
