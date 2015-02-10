package editor.model;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DocumentSection {

	private SectionType type;
	private String title;
	private Long sectionId;
	private Long sectionPosition;
	private Visibility state;
	private String organizationName;
	private String organizationDescription;
	private String role;

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate startDate;

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate endDate;

	private boolean isCurrent;
	private String location;
	private String roleDescription;
	private String highlights;

	private String description;

	public DocumentSection() {
	}

	public SectionType getType() {
		return type;
	}

	public void setType(SectionType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public Long getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(Long sectionPosition) {
		this.sectionPosition = sectionPosition;
	}

	public Visibility getState() {
		return state;
	}

	public void setState(Visibility state) {
		this.state = state;
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

	public boolean getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getHighlights() {
		return highlights;
	}

	public void setHighlights(String highlights) {
		this.highlights = highlights;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ResumeSection [type=" + type + ", title=" + title
				+ ", sectionId=" + sectionId + ", sectionPosition="
				+ sectionPosition + ", state=" + state + ", organizationName="
				+ organizationName + ", organizationDescription="
				+ organizationDescription + ", role=" + role + ", startDate="
				+ startDate + ", endDate=" + endDate + ", isCurrent="
				+ isCurrent + ", location=" + location + ", roleDescription="
				+ roleDescription + ", highlights=" + highlights
				+ ", description=" + description + "]";
	}

	// TODO: the following methods are here for convenience, will move them
	// later.

	public String asJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public static DocumentSection fromJSON(String json) {

		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(json, DocumentSection.class);
		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;
	}

}
