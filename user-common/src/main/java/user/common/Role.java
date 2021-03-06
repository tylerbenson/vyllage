package user.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Role {

	/**
	 *
	 */
	private final String role;

	public Role(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

}
