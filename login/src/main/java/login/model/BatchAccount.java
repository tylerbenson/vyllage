package login.model;

import lombok.ToString;

@ToString
public class BatchAccount {

	private String accounts;
	private String group;

	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
