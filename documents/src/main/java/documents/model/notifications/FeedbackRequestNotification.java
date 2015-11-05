package documents.model.notifications;

import user.common.web.NotifyFeedbackRequest;
import documents.domain.tables.records.FeedbackRequestNotificationRecord;

public class FeedbackRequestNotification extends AbstractNotification {

	// user who requested feedback.
	private final Long resumeUserId;

	private final Long resumeId;

	// @NonNull results in dead code here :/
	public FeedbackRequestNotification(FeedbackRequestNotificationRecord record) {
		super(record.getUserId(), record.getDateCreated().toLocalDateTime());

		this.resumeUserId = record.getResumeUserId();
		this.resumeId = record.getResumeId();

	}

	// @NonNull results in dead code here :/
	public FeedbackRequestNotification(
			NotifyFeedbackRequest notifyFeedbackRequest) {
		super(notifyFeedbackRequest.getUserId());

		this.resumeId = notifyFeedbackRequest.getResumeId();
		this.resumeUserId = notifyFeedbackRequest.getResumeUserId();
	}

	public Long getResumeId() {
		return resumeId;
	}

	public Long getResumeUserId() {
		return resumeUserId;
	}
}