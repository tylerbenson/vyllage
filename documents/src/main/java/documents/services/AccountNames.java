package documents.services;

import lombok.ToString;

@ToString
public class AccountNames {

	private String firstName;
	private String middleName;
	private String lastName;

	public AccountNames() {
	}

	public AccountNames(String firstName, String middleName, String lastName) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

}
