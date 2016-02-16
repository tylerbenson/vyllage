package documents.model.jobs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.ToString;
import documents.domain.tables.records.JobOffersRecord;

@ToString
public class JobOffer {

	private Long jobOfferId;

	/**
	 * User that created the job offer.
	 */
	private Long userId;

	/**
	 * Vyllage Organization owning the job offer.
	 */
	private Long organizationId;

	private JobType jobType;

	private JobExperience jobExperience;

	private BigDecimal salary;

	private String location;

	// not sure?
	private boolean requiresRelocation;

	private boolean remote;

	private LocalDateTime dateCreated;

	private LocalDateTime lastModified;

	private String company;

	private String role;

	private String description;

	private List<JobReponsibility> jobResponsibilities = new ArrayList<>();

	public JobOffer() {

	}

	public JobOffer(@NotNull JobOffersRecord record) {
		this.dateCreated = record.getDateCreated().toLocalDateTime();
		this.jobExperience = JobExperience.valueOf(record.getJobExperience());
		this.jobOfferId = record.getJobOfferId();
		this.jobType = JobType.valueOf(record.getJobType());
		this.lastModified = record.getLastModified().toLocalDateTime();
		this.location = record.getLocation();
		this.remote = record.getRemote();
		this.requiresRelocation = record.getRequiresRelocation();
		this.salary = record.getSalary();
		this.userId = record.getUserId();
		this.organizationId = record.getOrganizationId();
		this.company = record.getCompany();
		this.role = record.getRole();
		this.description = record.getDescription();

	}

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

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
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

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<JobReponsibility> getJobResponsibilities() {
		return jobResponsibilities;
	}

	public void setJobResponsibilities(
			List<JobReponsibility> jobResponsibilities) {
		this.jobResponsibilities = jobResponsibilities;
	}

}
