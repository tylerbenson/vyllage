package accounts.model.account.settings;

public class OrganizationSetting {
	private Long organizationMemberId;
	private Long organizationId;

	public Long getOrganizationMemberId() {
		return organizationMemberId;
	}

	public void setOrganizationMemberId(Long organizationMemberId) {
		this.organizationMemberId = organizationMemberId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

}
