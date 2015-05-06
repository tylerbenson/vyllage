package documents.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Used to determine if a user has received a notification.
 * 
 * @author uh
 *
 */
public class UserNotification {
	private Long userId;
	private LocalDateTime dateCreated;

	public UserNotification(Long userId) {
		this.userId = userId;
	}

	public UserNotification(Long userId, LocalDateTime dateCreated) {
		this.userId = userId;
		this.dateCreated = dateCreated;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public boolean wasSentToday() {
		return LocalDateTime.now(ZoneId.of("UTC")).getDayOfMonth() == getDateCreated()
				.getDayOfMonth();
	}
}
