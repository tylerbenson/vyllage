package user.common.web;

import lombok.NonNull;

public class NotifyFeedbackRequest {
	// user to which the request will be created.
	private final Long userId;
	private final Long resumeId;
	private final Long resumeUserId;

	public NotifyFeedbackRequest(@NonNull Long userId, @NonNull Long resumeId,
			@NonNull Long resumeUserId) {
		this.userId = userId;
		this.resumeId = resumeId;
		this.resumeUserId = resumeUserId;
	}

	public Long getResumeId() {
		return resumeId;
	}

	public Long getResumeUserId() {
		return resumeUserId;
	}

	public Long getUserId() {
		return userId;
	}
}
