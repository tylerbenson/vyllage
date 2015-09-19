package documents.model.document.sections;

import java.time.LocalDateTime;

import lombok.ToString;
import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@ToString(callSuper = true)
public class ProjectsSection extends DocumentSection {

	private String projectTitle;
	private String author;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime projectDate;
	private String projectImageUrl;

	private String projectDescription;
	private String role;
	private String roleDescription;

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDateTime getProjectDate() {
		return projectDate;
	}

	public void setProjectDate(LocalDateTime projectDate) {
		this.projectDate = projectDate;
	}

	public String getProjectImageUrl() {
		return projectImageUrl;
	}

	public void setProjectImageUrl(String projectImageUrl) {
		this.projectImageUrl = projectImageUrl;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
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

}
