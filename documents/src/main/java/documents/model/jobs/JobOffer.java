package documents.model.jobs;

import java.time.LocalDateTime;

import lombok.ToString;
import documents.indeed.JobType;

@ToString
public class JobOffer {

	private Long jobOfferId;

	private Long userId;

	private JobType jobType;

	private JobExperience jobExperience;

	private Double salary;

	private String location;

	// not sure?
	private boolean requiresRelocation;

	private boolean remote;

	private LocalDateTime dateCreated;

	private LocalDateTime lastModified;

	public Long getJobOfferId() {
		return jobOfferId;
	}

	public void setJobOfferId(Long jobOfferId) {
		this.jobOfferId = jobOfferId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public JobExperience getJobExperience() {
		return jobExperience;
	}

	public void setJobExperience(JobExperience experience) {
		this.jobExperience = experience;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public boolean isRequiresRelocation() {
		return requiresRelocation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setRequiresRelocation(boolean requiresRelocation) {
		this.requiresRelocation = requiresRelocation;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
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
