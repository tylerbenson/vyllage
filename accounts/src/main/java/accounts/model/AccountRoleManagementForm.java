package accounts.model;

import java.util.List;

import lombok.ToString;

@ToString
public class AccountRoleManagementForm {

	private Long organizationId;

	private List<Long> userIds;

	private List<String> roles;

	private boolean append;

	private String error = null;

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public boolean isInvalid() {
		boolean invalid = true;

		invalid = organizationId == null || !userIdsAreValid()
				|| !rolesAreValid();

		return invalid;
	}

	private boolean rolesAreValid() {
		if (roles == null || roles.isEmpty()) {
			setError("No Roles selected.");
			return false;
		}
		return true;
	}

	private boolean userIdsAreValid() {
		if (userIds == null || userIds.isEmpty()) {
			setError("No Users selected.");
			return false;
		}
		return true;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
