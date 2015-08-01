package user.common.constants;

public enum OrganizationEnum {
	VYLLAGE(0L), GUESTS(4L);

	private final Long organizationId;

	OrganizationEnum(Long organizationId) {
		this.organizationId = organizationId;
	}

	public Long getOrganizationId() {
		return organizationId;
	}
}
