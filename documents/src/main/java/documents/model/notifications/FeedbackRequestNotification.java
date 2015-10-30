package documents.model.notifications;

import java.time.LocalDateTime;

import lombok.NonNull;
import user.common.web.NotifyFeedbackRequest;
import documents.domain.tables.records.FeedbackRequestNotificationRecord;

public class FeedbackRequestNotification {
	// owner of the notification
	private final Long userId;

	private LocalDateTime dateCreated;

	// user who requested feedback.
	private final Long resumeUserId;

	private final Long resumeId;

	public FeedbackRequestNotification(
			@NonNull FeedbackRequestNotificationRecord record) {
		this.resumeUserId = record.getResumeUserId();
		this.userId = record.getUserId();
		this.dateCreated = record.getDateCreated().toLocalDateTime();
		this.resumeId = record.getResumeId();

	}

	public FeedbackRequestNotification(
			@NonNull NotifyFeedbackRequest notifyFeedbackRequest) {
		this.userId = notifyFeedbackRequest.getUserId();
		this.resumeId = notifyFeedbackRequest.getResumeId();
		this.resumeUserId = notifyFeedbackRequest.getResumeUserId();
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Long getResumeId() {
		return resumeId;
	}

	public Long getResumeUserId() {
		return resumeUserId;
	}
}