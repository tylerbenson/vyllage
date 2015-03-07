package accounts.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import accounts.domain.tables.records.RolesRecord;

@ToString
@EqualsAndHashCode
public class Role implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6420872157496814372L;
	private final String role;
	private final String userName;

	public Role(String role, String userName) {
		this.role = role;
		this.userName = userName;

	}

	public Role(RolesRecord record) {
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
