package accounts.model.account;

import lombok.ToString;

@ToString
public class AccountContact {
	private Long userId;
	private String address;
	private String email;
	private String phoneNumber;
	private String twitter;
	private String linkedIn;

	public AccountContact(Long userId, String address, String email,
			String phoneNumber, String twitter, String linkedIn) {
		super();
		this.userId = userId;
		this.address = address;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.twitter = twitter;
		this.linkedIn = linkedIn;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AccountContact() {
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
}
