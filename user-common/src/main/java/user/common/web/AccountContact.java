package user.common.web;

import lombok.ToString;
import user.common.User;

@ToString
public class AccountContact {
	private Long userId;
	private String address;
	private String email;
	private String phoneNumber;
	private String twitter;
	private String linkedIn;

	private String firstName;
	private String middleName;
	private String lastName;

	private String avatarUrl;

	private String tagline;

	private boolean isAdvisor;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AccountContact() {
	}

	/**
	 * AccountContact with basic user info. <br>
	 * userId <br>
	 * email <br>
	 * firstName <br>
	 * middleName <br>
	 * lastName <br>
	 *
	 * @param user
	 */
	public AccountContact(User user) {
		this.userId = user.getUserId();
		this.email = user.getUsername();
		this.firstName = user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName = user.getLastName();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
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

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public boolean isAdvisor() {
		return isAdvisor;
	}

	public void setAdvisor(boolean isAdvisor) {
		this.isAdvisor = isAdvisor;
	}

}
