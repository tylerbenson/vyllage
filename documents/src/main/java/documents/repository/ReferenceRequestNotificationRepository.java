package documents.repository;

import static documents.domain.tables.ReferenceRequestNotification.REFERENCE_REQUEST_NOTIFICATION;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.NonNull;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import documents.domain.tables.records.ReferenceRequestNotificationRecord;
import documents.model.notifications.ReferenceRequestNotification;

@Repository
public class ReferenceRequestNotificationRepository {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(CommentNotificationRepository.class.getName());

	@Inject
	private DSLContext sql;

	public List<ReferenceRequestNotification> get(Long userId) {

		Result<ReferenceRequestNotificationRecord> referenceRequestNotificationRecords = sql
				.fetch(REFERENCE_REQUEST_NOTIFICATION,
						REFERENCE_REQUEST_NOTIFICATION.USER_ID.eq(userId));

		if (referenceRequestNotificationRecords == null
				|| referenceRequestNotificationRecords.isEmpty())
			return Collections.emptyList();
		else
			return referenceRequestNotificationRecords.stream()
					.map(record -> new ReferenceRequestNotification(record))
					.collect(Collectors.toList());

	}

	public void save(
			@NonNull ReferenceRequestNotification referenceRequestNotification) {
		// they will be deleted when the user acknowledges them

		final ReferenceRequestNotificationRecord referenceRequestNotificationRecord = sql
				.fetchOne(
						REFERENCE_REQUEST_NOTIFICATION,
						REFERENCE_REQUEST_NOTIFICATION.USER_ID
								.eq(referenceRequestNotification.getUserId())
								.and(REFERENCE_REQUEST_NOTIFICATION.REFERENCE_REQUEST_USER_ID
										.eq(referenceRequestNotification
												.getReferenceRequestUserId())));

		// no update, we only care about the first one.
		if (referenceRequestNotificationRecord == null) {

			ReferenceRequestNotificationRecord newRecord = sql
					.newRecord(REFERENCE_REQUEST_NOTIFICATION);

			newRecord.setUserId(referenceRequestNotification.getUserId());
			newRecord.setReferenceRequestUserId(referenceRequestNotification
					.getReferenceRequestUserId());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.store();
		}
	}

	public void deleteAll(@NonNull Long userId) {
		sql.delete(REFERENCE_REQUEST_NOTIFICATION)
				.where(REFERENCE_REQUEST_NOTIFICATION.USER_ID.eq(userId))
				.execute();
	}

	public void delete(@NonNull Long userId, @NonNull Long otherUserId) {
		sql.delete(REFERENCE_REQUEST_NOTIFICATION)
				.where(REFERENCE_REQUEST_NOTIFICATION.USER_ID.eq(userId))
				.and(REFERENCE_REQUEST_NOTIFICATION.REFERENCE_REQUEST_USER_ID
						.eq(otherUserId)).execute();
	}

}
