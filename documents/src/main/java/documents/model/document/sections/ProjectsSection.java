package documents.model.document.sections;

import java.time.LocalDateTime;

import lombok.ToString;

@ToString(callSuper = true)
public class ProjectsSection extends DocumentSection {

	private String projectTitle;
	private String author;
	private LocalDateTime projectDate;
	private String projectImageUrl;

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

}
