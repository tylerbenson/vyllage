package accounts.model;

import org.springframework.security.core.GrantedAuthority;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import accounts.domain.tables.records.UserRolesRecord;
@ToString
@EqualsAndHashCode
public class UserRole implements GrantedAuthority {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6420872157496814372L;
	private final String role;
	private final String userName;

	public UserRole(String role, String userName) {
		this.role = role;
		this.userName = userName;

	}

	public UserRole(UserRolesRecord record) {
		this.role = record.getRole();
		this.userName = record.getUserName();
	}

	@Override
	public String getAuthority() {
		return this.role;
	}

	public String getUserName() {
		return userName;
	}
}
