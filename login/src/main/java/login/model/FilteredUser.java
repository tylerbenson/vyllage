package login.model;

import lombok.ToString;

@ToString
public class FilteredUser {
	private String name;

	public FilteredUser(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
