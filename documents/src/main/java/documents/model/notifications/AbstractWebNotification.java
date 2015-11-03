package documents.model.notifications;

import java.time.LocalDateTime;

import lombok.NonNull;
import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class AbstractWebNotification {

	private Long userId;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime dateCreated;

	private String userName;

	private Long otherUserId;

	public AbstractWebNotification() {
	}

	public AbstractWebNotification(@NonNull Long userId,
			@NonNull LocalDateTime dateCreated, @NonNull Long otherUserId) {
		this.userId = userId;
		this.dateCreated = dateCreated;
		this.otherUserId = otherUserId;
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getOtherUserId() {
		return otherUserId;
	}

}
