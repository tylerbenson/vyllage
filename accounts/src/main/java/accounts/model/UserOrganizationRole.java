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
	private Long auditUserId;

	public UserOrganizationRole(Long userId, Long organizationId, String role,
			Long auditUserId) {
		this.role = role;
		this.userId = userId;
		this.organizationId = organizationId;
		this.auditUserId = auditUserId;

	}

	public UserOrganizationRole(UserOrganizationRolesRecord record) {
		this.role = record.getRole();
		this.userId = record.getUserId();
		this.organizationId = record.getOrganizationId();
		this.auditUserId = record.getAuditUserId();
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

	public Long getAuditUserId() {
		return auditUserId;
	}

	public void setAuditUserId(Long auditUserId) {
		this.auditUserId = auditUserId;
	}
}
