package editor.model;

public class DocumentHeader {
	
	private String firstName;
	private String middleName;
	private String lastName;
	private String tagline;
	
	public DocumentHeader() {
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
	public String getTagline() {
		return tagline;
	}
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	@Override
	public String toString() {
		return "ResumeHeader [firstName=" + firstName + ", middleName="
				+ middleName + ", lastName=" + lastName + ", tagline="
				+ tagline + "]";
	}
	
	
	
}
