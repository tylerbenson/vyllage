package documents.model.notifications;

import org.springframework.util.Assert;

import documents.domain.tables.records.ReferenceRequestNotificationRecord;

public class ReferenceRequestNotification extends AbstractNotification {

	private final Long referenceRequestUserId;

	public ReferenceRequestNotification(Long userId, Long referenceRequestUserId) {
		super(userId);
		Assert.notNull(referenceRequestUserId);

		this.referenceRequestUserId = referenceRequestUserId;
	}

	public ReferenceRequestNotification(
			ReferenceRequestNotificationRecord record) {
		super(record.getUserId(), record.getDateCreated().toLocalDateTime());
		this.referenceRequestUserId = record.getReferenceRequestUserId();
	}

	public Long getReferenceRequestUserId() {
		return referenceRequestUserId;
	}

}
