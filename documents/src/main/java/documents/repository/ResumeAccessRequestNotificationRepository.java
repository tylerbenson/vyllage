package documents.repository;

import static documents.domain.tables.ResumeAccessRequestNotification.RESUME_ACCESS_REQUEST_NOTIFICATION;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import documents.domain.tables.records.ResumeAccessRequestNotificationRecord;
import documents.model.notifications.ResumeAccessRequestNotification;

@Repository
public class ResumeAccessRequestNotificationRepository {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(ResumeAccessRequestNotificationRepository.class
					.getName());
	@Inject
	private DSLContext sql;

	public List<ResumeAccessRequestNotification> get(@NotNull Long userId) {

		Result<ResumeAccessRequestNotificationRecord> referenceRequestNotificationRecords = sql
				.fetch(RESUME_ACCESS_REQUEST_NOTIFICATION,
						RESUME_ACCESS_REQUEST_NOTIFICATION.USER_ID.eq(userId));

		if (referenceRequestNotificationRecords == null
				|| referenceRequestNotificationRecords.isEmpty())
			return Collections.emptyList();
		else
			return referenceRequestNotificationRecords.stream()
					.map(record -> new ResumeAccessRequestNotification(record))
					.collect(Collectors.toList());

	}

	public void save(
			@NotNull ResumeAccessRequestNotification resumeAccessRequest) {
		// they will be deleted when the user acknowledges them

		final ResumeAccessRequestNotificationRecord resumeAccessRequestNotificationRecord = sql
				.fetchOne(
						RESUME_ACCESS_REQUEST_NOTIFICATION,
						RESUME_ACCESS_REQUEST_NOTIFICATION.USER_ID
								.eq(resumeAccessRequest.getUserId())
								.and(RESUME_ACCESS_REQUEST_NOTIFICATION.RESUME_REQUEST_USER_ID
										.eq(resumeAccessRequest
												.getResumeRequestUserId())));

		// no update, we only care about the first one.
		if (resumeAccessRequestNotificationRecord == null) {

			ResumeAccessRequestNotificationRecord newRecord = sql
					.newRecord(RESUME_ACCESS_REQUEST_NOTIFICATION);

			newRecord.setUserId(resumeAccessRequest.getUserId());
			newRecord.setResumeRequestUserId(resumeAccessRequest
					.getResumeRequestUserId());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.store();
		}
	}

	public void deleteAll(@NotNull Long userId) {
		sql.delete(RESUME_ACCESS_REQUEST_NOTIFICATION)
				.where(RESUME_ACCESS_REQUEST_NOTIFICATION.USER_ID.eq(userId))
				.execute();
	}

	public void delete(@NotNull Long userId, @NotNull Long otherUserId) {
		sql.delete(RESUME_ACCESS_REQUEST_NOTIFICATION)
				.where(RESUME_ACCESS_REQUEST_NOTIFICATION.USER_ID.eq(userId))
				.and(RESUME_ACCESS_REQUEST_NOTIFICATION.RESUME_REQUEST_USER_ID
						.eq(otherUserId)).execute();
	}
}
