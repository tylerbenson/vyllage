package connections.model;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class UserFilterResponse {
	private List<Contact> recent = new ArrayList<>();
	private List<Contact> recommended = new ArrayList<>();
	private List<Contact> other = new ArrayList<>();

	public List<Contact> getRecent() {
		return recent;
	}

	public void setRecent(List<Contact> recent) {
		this.recent = recent;
	}

	public List<Contact> getRecommended() {
		return recommended;
	}

	public void setRecommended(List<Contact> recommended) {
		this.recommended = recommended;
	}

	public List<Contact> getOther() {
		return other;
	}

	public void setOther(List<Contact> other) {
		this.other = other;
	}

}
