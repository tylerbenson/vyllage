package accounts.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import accounts.domain.tables.records.UserOrganizationRolesRecord;

@ToString
@EqualsAndHashCode
public class UserOrganizationRole implements GrantedAuthority {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6420872157496814372L;
	private final String role;
	private Long userId;
	private Long organizationId;

	// public UserOrganizationRole(String role, Long userId, Long
	// organizationId) {
	// this.role = role;
	// this.userId = userId;
	// this.organizationId = organizationId;
	//
	// }

	public UserOrganizationRole(Long userId, Long organizationId, String role) {
		this.role = role;
		this.userId = userId;
		this.organizationId = organizationId;

	}

	public UserOrganizationRole(UserOrganizationRolesRecord record) {
		this.role = record.getRole();
		this.userId = record.getUserId();
		this.organizationId = record.getOrganizationId();
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

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
}
