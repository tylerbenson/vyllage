package accounts.model.form;

import java.util.List;

import lombok.ToString;

@ToString
public class UserOrganizationForm {

	private Long userId;
	private List<Long> organizationIds;
	private String error;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
		if (organizationIds == null || organizationIds.isEmpty()) {
			// user id is not selected, if it's null it's an app error.
			setError("You must select at least one organization.");
			return true;
		}

		return false;
	}

}
