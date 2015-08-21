package documents.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

public class UserNotificationTest {

	@Test
	public void testWasSentToday() {
		UserNotification un = new UserNotification(0L, LocalDateTime.now());

		assertTrue(un.wasSentToday());

	}

	@Test
	public void testWasSentYesterday() {
		UserNotification un = new UserNotification(0L, LocalDateTime.now()
				.minusDays(1L));

		assertFalse(un.wasSentToday());

	}

}
