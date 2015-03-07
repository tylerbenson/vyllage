package accounts.model;

public class OrganizationMember {
	private Long organizationId;
	private Long userId;

	public OrganizationMember() {
	}

	public OrganizationMember(Long group_id, Long userId) {
		this.organizationId = group_id;
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
