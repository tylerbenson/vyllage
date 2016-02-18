package jobs.repository;

import static jobs.domain.tables.JobOpening.JOB_OPENING;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import jobs.domain.tables.records.JobOpeningRecord;
import jobs.model.JobOpening;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

@Repository
public class JobOpeningRepository {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(JobOpeningRepository.class
			.getName());

	private DSLContext sql;

	@Inject
	public JobOpeningRepository(DSLContext sql) {
		this.sql = sql;
	}

	public List<JobOpening> getAll() {
		Result<JobOpeningRecord> all = sql.fetch(JOB_OPENING);

		// logger.info("Returning job offers: \n" + all);

		if (all.isEmpty())
			return Collections.emptyList();

		return all.stream().map(JobOpening::new).collect(Collectors.toList());
	}

	public JobOpening save(@NotNull JobOpening jobOffer) {

		JobOpeningRecord existingRecord = sql.fetchOne(JOB_OPENING,
				JOB_OPENING.JOB_OPENING_ID.eq(jobOffer.getJobOpeningId()));

		if (existingRecord == null) {
			JobOpeningRecord newRecord = sql.newRecord(JOB_OPENING);

			newRecord.setCompany(jobOffer.getCompany());
			newRecord.setDescription(jobOffer.getDescription());

			newRecord.setJobExperience(jobOffer.getJobExperience().name());

			newRecord.setJobType(jobOffer.getJobType().name());

			newRecord.setLocation(jobOffer.getLocation());
			newRecord.setOrganizationId(jobOffer.getOrganizationId());
			newRecord.setRemote(jobOffer.isRemote());
			newRecord.setRequiresRelocation(jobOffer.isRequiresRelocation());
			newRecord.setSiteWide(jobOffer.isSiteWide());
			newRecord.setRole(jobOffer.getRole());
			newRecord.setSalary(jobOffer.getSalary());
			newRecord.setUserId(jobOffer.getUserId());

			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			newRecord.store();

			jobOffer.setJobOpeningId(newRecord.getJobOpeningId());

			jobOffer.setDateCreated(newRecord.getDateCreated()
					.toLocalDateTime());

			jobOffer.setLastModified(newRecord.getLastModified()
					.toLocalDateTime());

		} else {

			existingRecord.setCompany(jobOffer.getCompany());
			existingRecord.setDescription(jobOffer.getDescription());

			existingRecord.setJobExperience(jobOffer.getJobExperience().name());

			existingRecord.setJobType(jobOffer.getJobType().name());

			existingRecord.setLocation(jobOffer.getLocation());
			existingRecord.setOrganizationId(jobOffer.getOrganizationId());
			existingRecord.setRemote(jobOffer.isRemote());
			existingRecord.setRequiresRelocation(jobOffer
					.isRequiresRelocation());
			existingRecord.setSiteWide(jobOffer.isSiteWide());
			existingRecord.setRole(jobOffer.getRole());
			existingRecord.setSalary(jobOffer.getSalary());
			existingRecord.setUserId(jobOffer.getUserId());

			existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			existingRecord.store();

			jobOffer.setLastModified(existingRecord.getLastModified()
					.toLocalDateTime());
		}

		return jobOffer;
	}

	public List<JobOpening> getAllByOrganization(@NotNull Long organizationId) {

		Result<JobOpeningRecord> all = sql.fetch(JOB_OPENING,
				JOB_OPENING.ORGANIZATION_ID.eq(organizationId));

		// logger.info("Returning job offers: \n" + all);

		if (all.isEmpty())
			return Collections.emptyList();

		return all.stream().map(JobOpening::new).collect(Collectors.toList());

	}

	public JobOpening get(Long jobOfferId) {
		return sql.fetchOne(JOB_OPENING,
				JOB_OPENING.JOB_OPENING_ID.eq(jobOfferId)).into(
				JobOpening.class);
	}

	public List<JobOpening> getSiteWideJobOpenings() {

		Result<JobOpeningRecord> result = sql.fetch(JOB_OPENING,
				JOB_OPENING.SITE_WIDE.eq(true));

		if (result.isEmpty())
			return Collections.emptyList();

		return result.stream().map(JobOpening::new)
				.collect(Collectors.toList());
	}

}
