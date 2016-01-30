package documents.model.document.sections;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import util.dateSerialization.LocalDateDeserializer;
import util.dateSerialization.LocalDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EducationSection extends DocumentSection {

	private String organizationName;
	private String organizationDescription;

	private String role;
	private String roleDescription;

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate startDate;

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate endDate;

	private String location;

	private boolean isCurrent;

	private List<String> highlights = new LinkedList<>();

	public EducationSection() {
		super();
		this.setType(SectionType.EDUCATION_SECTION.type());
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationDescription() {
		return organizationDescription;
	}

	public void setOrganizationDescription(String organizationDescription) {
		this.organizationDescription = organizationDescription;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean getIsCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public List<String> getHighlights() {
		return highlights;
	}

	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
	}

	@Override
	public String asTxt() {
		StringBuilder sb = new StringBuilder();
		sb.append("Education");
		sb.append("\n");

		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

		if (this.getStartDate() != null)
			sb.append(this.getStartDate().format(formatter));

		if (this.getStartDate() != null && this.getEndDate() != null)
			sb.append(" to ").append(this.getEndDate().format(formatter));
		else
			sb.append(" ");

		sb.append(this.getOrganizationName()).append(" ");
		sb.append(this.getOrganizationDescription()).append(" ");
		sb.append(this.getLocation()).append("\n\n");

		sb.append(this.getRole()).append("\n");
		sb.append(this.getRoleDescription());

		return sb.append("\n").toString();
	}

}
