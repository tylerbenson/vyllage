package accounts.model.account;

import java.time.LocalDateTime;

public class AccountSettings {

	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String role;
	private LocalDateTime graduationDate;
	private LocalDateTime memberSince;
	private LocalDateTime lastUpdate;
	private PersonalInformation userPersonalInformation;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getGraduationDate() {
		return graduationDate;
	}

	public void setGraduationDate(LocalDateTime localDateTime) {
		this.graduationDate = localDateTime;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public PersonalInformation getUserPersonalInformation() {
		return userPersonalInformation;
	}

	public void setUserPersonalInformation(
			PersonalInformation userPersonalInformation) {
		this.userPersonalInformation = userPersonalInformation;
	}

}
