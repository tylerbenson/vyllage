package documents.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import documents.domain.tables.records.CommentNotificationRecord;
import documents.model.notifications.CommentNotification;

public class UserNotificationTest {

	@Test
	public void testWasSentToday() {

		CommentNotificationRecord record = new CommentNotificationRecord(0L,
				0L, 0L, 0L, "Title", Timestamp.valueOf(LocalDateTime.now(ZoneId
						.of("UTC"))));

		CommentNotification un = new CommentNotification(record);

		assertTrue(un.wasSentToday());

	}

	@Test
	public void testWasSentYesterday() {
		CommentNotificationRecord record = new CommentNotificationRecord(0L,
				0L, 0L, 0L, "Title", Timestamp.valueOf(LocalDateTime.now(
						ZoneId.of("UTC")).minusDays(1L)));

		CommentNotification un = new CommentNotification(record);

		assertFalse(un.wasSentToday());

	}

}
