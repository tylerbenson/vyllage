package documents.model.jobs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import util.dateSerialization.DocumentLocalDateTimeDeserializer;
import util.dateSerialization.DocumentLocalDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import documents.domain.tables.records.JobOffersRecord;
import documents.services.indeed.IndeedResult;

@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(value = { "error", "userId", "jobOfferId", "valid" })
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

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime dateCreated;

	@JsonSerialize(using = DocumentLocalDateTimeSerializer.class)
	@JsonDeserialize(using = DocumentLocalDateTimeDeserializer.class)
	private LocalDateTime lastModified;

	private String company;

	private String role;

	private String description;

	private boolean siteWide;

	private String error;

	public JobOffer() {

	}

	public JobOffer(@NonNull JobOffersRecord record) {
		this.dateCreated = record.getDateCreated().toLocalDateTime();
		this.lastModified = record.getLastModified().toLocalDateTime();

		this.jobExperience = JobExperience.valueOf(record.getJobExperience());
		this.jobType = JobType.valueOf(record.getJobType());

		this.jobOfferId = record.getJobOfferId();
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

	public JobOffer(@NonNull IndeedResult result) {

		this.dateCreated = getIndeedDate(result);
		// same
		this.lastModified = this.dateCreated;

		this.company = result.getCompany();
		this.role = result.getJobtitle();
		this.description = result.getSnippet();

		this.location = getIndeedLocation(result);

		// hmm, these are not present.
		this.remote = false;
		this.requiresRelocation = false;
		this.salary = new BigDecimal(0);

		// just in case we do something with this one on the frontend too.
		this.siteWide = false;

	}

	/**
	 * Parses the indeed date created into LocalDateTime.
	 * 
	 * @param result
	 * @return date created
	 */
	protected LocalDateTime getIndeedDate(IndeedResult result) {
		// from indeed xml response : "date": "Wed, 25 Nov 2015 08:29:28 GMT"
		String YYYY_MM_DD = "E, dd MMM yyyy HH:mm:ss z";

		// thre's no way to know if it's null, it could be recent or from a long
		// time ago.
		if (StringUtils.isBlank(result.getDate()))
			return null;

		try {
			return LocalDateTime.parse(result.getDate(),
					DateTimeFormatter.ofPattern(YYYY_MM_DD));
		} catch (DateTimeParseException e) {
			// date stays null.
		}

		return null;
	}

	/**
	 * Creates a full location based on city, state and county fields. <br/>
	 * If they are not present tries with FormattedLocation and
	 * FormattedLocationFull
	 * 
	 * @param result
	 * @return
	 */
	protected String getIndeedLocation(IndeedResult result) {
		String location = result.getCity() + ", " + result.getState() + ", "
				+ result.getCountry();

		if (StringUtils.isBlank(location))
			location = result.getFormattedLocation();

		if (StringUtils.isBlank(location))
			location = result.getFormattedLocationFull();
		return location;
	}

	/**
	 * Checks if the fields Company, Description, Location, Role, Job
	 * Experience, Job Type are present.
	 * 
	 * @return true | false
	 */
	public boolean isValid() {
		boolean someBlank = StringUtils.isAnyBlank(this.company,
				this.description, this.location, this.role);

		boolean missingEnums = this.jobExperience == null
				|| this.jobType == null;

		if (someBlank || missingEnums)
			setError("Please complete required fields.");

		return !someBlank && !missingEnums;
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

}
