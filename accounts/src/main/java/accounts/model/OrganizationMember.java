package accounts.model;

public class OrganizationMember {
	private Long organizationId;
	private String userName;

	public OrganizationMember() {
	}

	public OrganizationMember(Long group_id, String userName) {
		this.organizationId = group_id;
		this.userName = userName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
