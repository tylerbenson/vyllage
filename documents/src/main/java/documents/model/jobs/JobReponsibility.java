package documents.model.jobs;

import java.time.LocalDateTime;

import lombok.ToString;
import documents.domain.tables.records.JobResponsibilityRecord;

@ToString
public class JobReponsibility {
	private Long jobResponsibilityId;

	private String description;

	private LocalDateTime dateCreated;

	private LocalDateTime lastModified;

	public JobReponsibility(JobResponsibilityRecord record) {
		this.jobResponsibilityId = record.getJobResponsibilityId();
		this.description = record.getDescription();
		this.dateCreated = record.getDateCreated().toLocalDateTime();
		this.lastModified = record.getLastModified().toLocalDateTime();
	}

	public Long getJobResponsibilityId() {
		return jobResponsibilityId;
	}

	public void setJobResponsibilityId(Long jobResponsibilityId) {
		this.jobResponsibilityId = jobResponsibilityId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
}
