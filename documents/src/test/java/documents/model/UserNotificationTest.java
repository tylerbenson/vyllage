package documents.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import documents.model.notifications.CommentNotification;

public class UserNotificationTest {

	@Test
	public void testWasSentToday() {
		CommentNotification un = new CommentNotification(0L,
				LocalDateTime.now(ZoneId.of("UTC")), 0L);

		assertTrue(un.wasSentToday());

	}

	@Test
	public void testWasSentYesterday() {
		CommentNotification un = new CommentNotification(0L, LocalDateTime.now(
				ZoneId.of("UTC")).minusDays(1L), 0L);

		assertFalse(un.wasSentToday());

	}

}
