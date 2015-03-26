package accounts.model.account;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString
public class PersonalInformation {
	private Long userId;
	private String emailUpdates;
	private LocalDateTime graduationDate;
	private String phoneNumber;
	private String address;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmailUpdates() {
		return emailUpdates;
	}

	public void setEmailUpdates(String emailUpdates) {
		this.emailUpdates = emailUpdates;
	}

	public LocalDateTime getGraduationDate() {
		return graduationDate;
	}

	public void setGraduationDate(LocalDateTime graduationDate) {
		this.graduationDate = graduationDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
