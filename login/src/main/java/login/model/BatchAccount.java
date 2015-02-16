package login.model;

import lombok.ToString;

@ToString
public class BatchAccount {

	private String emails;
	private Long group;

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public Long getGroup() {
		return group;
	}

	public void setGroup(Long group) {
		this.group = group;
	}
}
