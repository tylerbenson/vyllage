package documents.model.notifications;

public class WebFeedbackRequestNotification extends AbstractWebNotification {

	private Long resumeId;

	private Long resumeUserId;

	public WebFeedbackRequestNotification() {
	}

	public WebFeedbackRequestNotification(
			FeedbackRequestNotification feedbackRequestNotification) {
		super(feedbackRequestNotification.getUserId(),
				feedbackRequestNotification.getDateCreated());

		this.resumeId = feedbackRequestNotification.getResumeId();
		this.resumeUserId = feedbackRequestNotification.getResumeUserId();
	}

	public Long getResumeId() {
		return resumeId;
	}

	public Long getResumeUserId() {
		return resumeUserId;
	}
}
