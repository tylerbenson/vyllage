package accounts.model.form;

import lombok.ToString;
import user.common.Organization;

@ToString
public class OrganizationOptionForm {
	private Long organizationId;
	private String organizationName;
	private boolean selected;

	public OrganizationOptionForm(Organization organization, boolean selected) {
		this.organizationId = organization.getOrganizationId();
		this.organizationName = organization.getOrganizationName();
		this.selected = selected;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
