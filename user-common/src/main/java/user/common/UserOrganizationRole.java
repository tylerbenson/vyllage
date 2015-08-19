package user.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

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
