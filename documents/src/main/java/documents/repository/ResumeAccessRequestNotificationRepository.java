package documents.repository;

import static documents.domain.tables.ResumeAccessRequestNotification.RESUME_ACCESS_REQUEST_NOTIFICATION;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

	public List<ResumeAccessRequestNotification> get(Long userId) {

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

	public void save(ResumeAccessRequestNotification resumeAccessRequest) {

	}
}
