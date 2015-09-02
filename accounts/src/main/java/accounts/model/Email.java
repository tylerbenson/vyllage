package accounts.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class Email {
	private Long emailId;
	private Long userId;
	private String email;
	private boolean defaultEmail;
	private boolean confirmed;
	private LocalDateTime dateCreated;
	private LocalDateTime lastModified;

	public Long getEmailId() {
		return emailId;
	}

	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
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

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

}
