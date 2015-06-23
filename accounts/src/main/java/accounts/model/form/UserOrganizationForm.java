package accounts.model.form;

import java.util.List;

import lombok.ToString;

@ToString
public class UserOrganizationForm {

	private Long userId;
	private List<Long> organizationIds;
	private List<String> roles;
	private String error;

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

	public List<Long> getOrganizationIds() {
		return organizationIds;
	}

	public void setOrganizationIds(List<Long> organizationIds) {
		this.organizationIds = organizationIds;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isInvalid() {
		if (organizationIds == null || organizationIds.isEmpty()
				|| roles == null || roles.isEmpty()) {
			// user id is not selected, if it's null it's an app error.
			setError("You must select at least one organization and role.");
			return true;
		}

		return false;
	}

}
