package editor.model;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class Document {
	private Long id;
	private Account account;
	private Boolean visibility;
	private LocalDateTime dateCreated;
	private LocalDateTime lastModified;

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getDateCreated() {
		return this.dateCreated;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public LocalDateTime getLastModified() {
		return this.lastModified;
	}

}
