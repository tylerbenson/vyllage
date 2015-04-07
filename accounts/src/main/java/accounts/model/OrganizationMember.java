package accounts.model;

public class OrganizationMember {
	private Long organizationId;
	private Long userId;

	public OrganizationMember() {
	}

	public OrganizationMember(Long organizationId, Long userId) {
		this.organizationId = organizationId;
		this.userId = userId;
	}

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

}
