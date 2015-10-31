package documents.repository;

import static documents.domain.tables.CommentNotification.COMMENT_NOTIFICATION;

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

import documents.domain.tables.records.CommentNotificationRecord;
import documents.model.notifications.CommentNotification;

@Repository
public class CommentNotificationRepository {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(CommentNotificationRepository.class.getName());

	@Inject
	private DSLContext sql;

	public List<CommentNotification> get(@NonNull Long userId) {
		Result<CommentNotificationRecord> commentNotificationRecords = sql
				.fetch(COMMENT_NOTIFICATION,
						COMMENT_NOTIFICATION.USER_ID.eq(userId));

		if (commentNotificationRecords == null
				|| commentNotificationRecords.isEmpty())
			return Collections.emptyList();
		else
			return commentNotificationRecords.stream()
					.map(record -> new CommentNotification(record))
					.collect(Collectors.toList());

	}

	public void save(@NonNull CommentNotification commentNotification) {
		// they will be deleted when the user acknowledges them

		final CommentNotificationRecord commentNotificationRecord = sql
				.fetchOne(
						COMMENT_NOTIFICATION,
						COMMENT_NOTIFICATION.USER_ID
								.eq(commentNotification.getUserId())
								.and(COMMENT_NOTIFICATION.COMMENT_ID
										.eq(commentNotification.getCommentId())));

		// no update, we only care about the first one.
		if (commentNotificationRecord == null) {

			CommentNotificationRecord newRecord = sql
					.newRecord(COMMENT_NOTIFICATION);

			newRecord.setUserId(commentNotification.getUserId());
			newRecord.setCommentUserId(commentNotification.getCommentUserId());
			newRecord.setCommentId(commentNotification.getCommentId());
			newRecord.setSectionTitle(commentNotification.getSectionTitle());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.store();
		}
	}

	public void deleteAll(@NonNull Long userId) {
		sql.delete(COMMENT_NOTIFICATION)
				.where(COMMENT_NOTIFICATION.USER_ID.eq(userId)).execute();
	}
}
