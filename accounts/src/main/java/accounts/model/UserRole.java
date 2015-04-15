package accounts.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import accounts.domain.tables.records.UserRolesRecord;

@ToString
@EqualsAndHashCode
public class UserRole implements GrantedAuthority {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6420872157496814372L;
	private final String role;
	private Long userId;

	public UserRole(String role, Long userId) {
		this.role = role;
		this.userId = userId;

	}

	public UserRole(UserRolesRecord record) {
		this.role = record.getRole();
		this.userId = record.getUserId();
	}

	@Override
	public String getAuthority() {
		return this.role;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
