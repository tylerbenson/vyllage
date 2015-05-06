package documents.repository;

import static documents.domain.tables.UserNotification.USER_NOTIFICATION;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import documents.domain.tables.records.UserNotificationRecord;
import documents.model.UserNotification;

@Repository
public class UserNotificationRepository {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(UserNotificationRepository.class.getName());

	@Autowired
	private DSLContext sql;

	public Optional<UserNotification> get(Long userId) {
		UserNotificationRecord userNotificationRecord = sql
				.fetchOne(USER_NOTIFICATION);

		if (userNotificationRecord == null) {
			return Optional.empty();
		} else
			return Optional.of(new UserNotification(userNotificationRecord
					.getUserId(), userNotificationRecord.getDateCreated()
					.toLocalDateTime()));
	}

	public void save(UserNotification userNotification) {
		sql.delete(USER_NOTIFICATION)
				.where(USER_NOTIFICATION.USER_ID.eq(userNotification
						.getUserId())).execute();

		UserNotificationRecord newRecord = sql.newRecord(USER_NOTIFICATION);

		newRecord.setUserId(userNotification.getUserId());
		newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
				.of("UTC"))));
		newRecord.store();
	}
}
