package editor.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import editor.model.constants.AccountVisibility;

public class AccountSettings {

	private String name;
	private String email;
	private String status;
	private AccountVisibility visibility;
	private LocalDate graduationDate;
	private LocalDateTime memberSince;
	private LocalDateTime lastUpdate;
	private List<Notification> notifications = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AccountVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(AccountVisibility visibility) {
		this.visibility = visibility;
	}

	public LocalDate getGraduationDate() {
		return graduationDate;
	}

	public void setGraduationDate(LocalDate graduationDate) {
		this.graduationDate = graduationDate;
	}

	public LocalDateTime getMemberSince() {
		return memberSince;
	}

	public void setMemberSince(LocalDateTime memberSince) {
		this.memberSince = memberSince;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notification) {
		this.notifications = notification;
	}

}
