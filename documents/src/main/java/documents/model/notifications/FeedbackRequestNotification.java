package documents.model.notifications;

import java.time.LocalDateTime;

import lombok.NonNull;
import user.common.web.NotifyFeedbackRequest;

public class FeedbackRequestNotification {
	// owner of the notification
	private final Long userId;

	private LocalDateTime dateCreated;

	// user who requested feedback.
	private final Long resumeUserId;

	private final Long resumeId;

	public FeedbackRequestNotification(@NonNull Long userId,
			@NonNull Long resumeUserId, @NonNull LocalDateTime dateCreated,
			@NonNull Long resumeId) {
		this.resumeUserId = resumeUserId;
		this.userId = userId;
		this.dateCreated = dateCreated;
		this.resumeId = resumeId;

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