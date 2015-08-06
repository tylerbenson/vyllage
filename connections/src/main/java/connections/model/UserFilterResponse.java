package connections.model;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import user.common.web.AccountContact;

@ToString
public class UserFilterResponse {
	private List<AccountContact> recent = new ArrayList<>();
	private List<AccountContact> recommended = new ArrayList<>();

	public List<AccountContact> getRecent() {
		return recent;
	}

	public void setRecent(List<AccountContact> recent) {
		this.recent = recent;
	}

	public List<AccountContact> getRecommended() {
		return recommended;
	}

	public void setRecommended(List<AccountContact> recommended) {
		this.recommended = recommended;
	}

}
