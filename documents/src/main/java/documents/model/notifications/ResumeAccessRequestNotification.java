package documents.model.notifications;

import org.springframework.util.Assert;

import documents.domain.tables.records.ResumeAccessRequestNotificationRecord;

public class ResumeAccessRequestNotification extends AbstractNotification {

	private final Long resumeRequestUserId;

	public ResumeAccessRequestNotification(Long userId, Long resumeRequestUserId) {
		super(userId);
		Assert.notNull(resumeRequestUserId);
		this.resumeRequestUserId = resumeRequestUserId;
	}

	public ResumeAccessRequestNotification(
			ResumeAccessRequestNotificationRecord record) {
		super(record.getUserId(), record.getDateCreated().toLocalDateTime());
		this.resumeRequestUserId = record.getResumeRequestUserId();
	}

	public Long getResumeRequestUserId() {
		return resumeRequestUserId;
	}

}
