package accounts.model.form;

import java.util.List;

import lombok.ToString;

@ToString
public class UserRoleManagementForm {

	private Long organizationId;

	private Long userId;

	private List<String> roles;

	private String error = null;

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isInvalid() {
		boolean invalid = organizationId == null || userId == null
				|| roles == null || roles.isEmpty();

		setError("You must select an organization, user and roles to complete this operation.");

		return invalid;
	}
}
