package accounts.constants;

public enum OrganizationEnum {
	VYLLAGE(0L);

	private final Long organizationId;

	OrganizationEnum(Long organizationId) {
		this.organizationId = organizationId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}
}
