package connections.service;

import lombok.ToString;

@ToString
public class FilteredUser {
	private Long userId;
	private String name;

	public FilteredUser() {
	}

	public FilteredUser(Long userId, String name) {
		super();
		this.userId = userId;
		this.name = name;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

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
