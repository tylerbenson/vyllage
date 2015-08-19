package accounts.model;

import lombok.ToString;

@ToString
public class BatchAccount {

	private String txt;
	private Long organization;
	private String role;

	public String getTxt() {
		return txt;
	}

	public void setTxt(String emails) {
		this.txt = emails;
	}

	public Long getOrganization() {
		return organization;
	}

	public void setOrganization(Long group) {
		this.organization = group;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean hasErrors() {
		return txt == null || txt.isEmpty() || organization == null
				|| role == null;
	}

}
