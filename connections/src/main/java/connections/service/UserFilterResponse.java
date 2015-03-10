package connections.service;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class UserFilterResponse {
	private List<FilteredUser> recent = new ArrayList<>();
	private List<FilteredUser> recommended = new ArrayList<>();
	private List<FilteredUser> other = new ArrayList<>();

	public List<FilteredUser> getRecent() {
		return recent;
	}

	public void setRecent(List<FilteredUser> recent) {
		this.recent = recent;
	}

	public List<FilteredUser> getRecommended() {
		return recommended;
	}

	public void setRecommended(List<FilteredUser> recommended) {
		this.recommended = recommended;
	}

	public List<FilteredUser> getOther() {
		return other;
	}

	public void setOther(List<FilteredUser> other) {
		this.other = other;
	}

}
