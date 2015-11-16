package documents.model.notifications;

import java.time.LocalDateTime;

import lombok.NonNull;

public class AbstractNotification {
	// owner of the notification
	protected final Long userId;
	private LocalDateTime dateCreated;

	public AbstractNotification(@NonNull Long userId) {
		this.userId = userId;

	}

	public AbstractNotification(@NonNull Long userId,
			@NonNull LocalDateTime dateCreated) {
		this.userId = userId;
		this.dateCreated = dateCreated;
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

}
