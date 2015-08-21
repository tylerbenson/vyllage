package user.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Organization {

	private Long organizationId;
	private String organizationName;

	public Organization() {

	}

	public Organization(Long organizationId, String organizationName) {
		this.organizationId = organizationId;
		this.organizationName = organizationName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
}
