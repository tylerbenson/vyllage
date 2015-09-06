package accounts.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Email {
	private Long emailId;
	private Long userId;
	private String email;
	private boolean defaultEmail;
	private boolean confirmed;

	public Email(@NonNull Long emailId, @NonNull Long userId,
			@NonNull String email, boolean defaultEmail, boolean confirmed) {
		super();
		this.emailId = emailId;
		this.userId = userId;
		this.email = email;
		this.defaultEmail = defaultEmail;
		this.confirmed = confirmed;
	}

	public Email(@NonNull Long userId, @NonNull String email,
			boolean defaultEmail, boolean confirmed) {
		super();
		this.userId = userId;
		this.email = email;
		this.defaultEmail = defaultEmail;
		this.confirmed = confirmed;
	}

	public Long getEmailId() {
		return emailId;
	}

	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(@NonNull Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(@NonNull String email) {
		this.email = email;
	}

	public boolean isDefaultEmail() {
		return defaultEmail;
	}

	public void setDefaultEmail(boolean defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

}
