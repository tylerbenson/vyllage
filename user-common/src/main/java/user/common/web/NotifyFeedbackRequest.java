package user.common.web;

import lombok.NonNull;
import lombok.ToString;

@ToString
public class NotifyFeedbackRequest {
	// user to which the request will be created.
	private Long userId;
	private Long resumeId;
	private Long resumeUserId;

	public NotifyFeedbackRequest() {
	}

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

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setResumeId(Long resumeId) {
		this.resumeId = resumeId;
	}

	public void setResumeUserId(Long resumeUserId) {
		this.resumeUserId = resumeUserId;
	}
}
