package documents.repository;

import static documents.domain.tables.FeedbackRequestNotification.FEEDBACK_REQUEST_NOTIFICATION;

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

import documents.domain.tables.records.FeedbackRequestNotificationRecord;
import documents.model.notifications.FeedbackRequestNotification;

@Repository
public class FeedbackRequestNotificationRepository {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(FeedbackRequestNotificationRepository.class.getName());

	@Inject
	private DSLContext sql;

	public List<FeedbackRequestNotification> get(@NonNull Long userId) {
		Result<FeedbackRequestNotificationRecord> feedbackNotificationRecords = sql
				.fetch(FEEDBACK_REQUEST_NOTIFICATION,
						FEEDBACK_REQUEST_NOTIFICATION.USER_ID.eq(userId));

		if (feedbackNotificationRecords == null
				|| feedbackNotificationRecords.isEmpty())
			return Collections.emptyList();
		else
			return feedbackNotificationRecords.stream()
					.map(record -> new FeedbackRequestNotification(record))
					.collect(Collectors.toList());
	}

	public void save(
			@NonNull FeedbackRequestNotification feedbackRequestNotification) {

		final FeedbackRequestNotificationRecord feedbackNotificationRecord = sql
				.fetchOne(
						FEEDBACK_REQUEST_NOTIFICATION,
						FEEDBACK_REQUEST_NOTIFICATION.USER_ID.eq(
								feedbackRequestNotification.getUserId()).and(
								FEEDBACK_REQUEST_NOTIFICATION.RESUME_ID
										.eq(feedbackRequestNotification
												.getResumeId())));

		// no update, we only care about the first one.
		if (feedbackNotificationRecord == null) {

			FeedbackRequestNotificationRecord newRecord = sql
					.newRecord(FEEDBACK_REQUEST_NOTIFICATION);

			newRecord.setUserId(feedbackRequestNotification.getUserId());
			newRecord.setResumeUserId(feedbackRequestNotification
					.getResumeUserId());
			newRecord.setResumeId(feedbackRequestNotification.getResumeId());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.store();
		}

	}

	public void deleteAll(@NonNull Long userId) {
		sql.delete(FEEDBACK_REQUEST_NOTIFICATION)
				.where(FEEDBACK_REQUEST_NOTIFICATION.USER_ID.eq(userId))
				.execute();
	}

	public void delete(@NonNull Long userId, @NonNull Long resumeId) {
		sql.delete(FEEDBACK_REQUEST_NOTIFICATION)
				.where(FEEDBACK_REQUEST_NOTIFICATION.USER_ID.eq(userId))
				.and(FEEDBACK_REQUEST_NOTIFICATION.RESUME_ID.eq(resumeId))
				.execute();
	}

}
