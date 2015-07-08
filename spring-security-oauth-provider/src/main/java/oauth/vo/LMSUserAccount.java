package oauth.vo;

import oauth.model.LMSUserRole;

public class LMSUserAccount {

	private Long userId;
	private String userName ;
	private String firstName;
	private String middleName;
	private String lastName;
	private String emailAddress ;
	private String phoneNumber;
	private LMSUserRole lmsUserRole ;
	

	public LMSUserAccount() {
		
	}

	public LMSUserAccount(Long userId, String userName , String firstName, String middleName, String lastName, String emailAddress , LMSUserRole lmsUserRole ) {
		super();
		this.userId = userId;
		this.userName = userName ;
		this.firstName = firstName;
		this.setMiddleName(middleName);
		this.setLastName(lastName);
		this.emailAddress = emailAddress ;
		this.setLmsUserRole(lmsUserRole) ;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LMSUserRole getLmsUserRole() {
		return lmsUserRole;
	}

	public void setLmsUserRole(LMSUserRole lmsUserRole) {
		this.lmsUserRole = lmsUserRole;
	}
}
