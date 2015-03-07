package accounts.model;

import lombok.ToString;

@ToString
public class BatchAccount {

	private String emails;
	private Long organization;

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public Long getOrganization() {
		return organization;
	}

	public void setOrganization(Long group) {
		this.organization = group;
	}

	public boolean hasErrors() {
		return emails == null || emails.isEmpty() || organization == null;
	}
}
