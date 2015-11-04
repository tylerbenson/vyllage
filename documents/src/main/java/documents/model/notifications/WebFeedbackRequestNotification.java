package documents.model.notifications;

public class WebFeedbackRequestNotification extends AbstractWebNotification {

	private Long resumeId;

	public WebFeedbackRequestNotification() {
	}

	public WebFeedbackRequestNotification(
			FeedbackRequestNotification feedbackRequestNotification) {
		super(feedbackRequestNotification.getUserId(),
				feedbackRequestNotification.getDateCreated(),
				feedbackRequestNotification.getResumeUserId());

		this.resumeId = feedbackRequestNotification.getResumeId();
		this.setType("WebFeedbackRequestNotification");
	}

	public Long getResumeId() {
		return resumeId;
	}

}
