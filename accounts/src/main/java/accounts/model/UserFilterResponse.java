package accounts.model;

import java.util.ArrayList;
import java.util.List;

import accounts.model.account.AccountNames;
import lombok.ToString;

@ToString
public class UserFilterResponse {
	private List<AccountNames> recent = new ArrayList<>();
	private List<AccountNames> recommended = new ArrayList<>();
	private List<AccountNames> other = new ArrayList<>();

	public List<AccountNames> getRecent() {
		return recent;
	}

	public void setRecent(List<AccountNames> recent) {
		this.recent = recent;
	}

	public List<AccountNames> getRecommended() {
		return recommended;
	}

	public void setRecommended(List<AccountNames> recommended) {
		this.recommended = recommended;
	}

	public List<AccountNames> getOther() {
		return other;
	}

	public void setOther(List<AccountNames> other) {
		this.other = other;
	}

}
