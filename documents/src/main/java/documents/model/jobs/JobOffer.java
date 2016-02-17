package documents.model.jobs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.jooq.tools.StringUtils;

import documents.domain.tables.records.JobOffersRecord;

@ToString
@EqualsAndHashCode
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

	private boolean siteWide;

	private String error;

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
		this.siteWide = record.getSiteWide();

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

	public boolean isSiteWide() {
		return siteWide;
	}

	public void setSiteWide(boolean siteWide) {
		this.siteWide = siteWide;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isValid() {
		boolean someBlank = StringUtils.isBlank(this.company)
				|| StringUtils.isBlank(this.description)
				|| StringUtils.isBlank(this.location)
				|| StringUtils.isBlank(this.role);

		boolean missingEnums = this.jobExperience == null
				|| this.jobType == null;

		if (someBlank || missingEnums)
			setError("Please complete required fields.");

		return !someBlank && !missingEnums;
	}

}
