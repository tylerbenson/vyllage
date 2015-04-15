package connections.model;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class UserFilterResponse {
	private List<AccountNames> recent = new ArrayList<>();
	private List<AccountNames> recommended = new ArrayList<>();

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

}
