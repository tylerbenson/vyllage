package documents.repository;

import static documents.domain.tables.JobOffers.JOB_OFFERS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import documents.domain.tables.records.JobOffersRecord;
import documents.model.jobs.JobOffer;

@Repository
public class JobOffersRepository {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(JobOffersRepository.class
			.getName());

	private DSLContext sql;

	@Inject
	public JobOffersRepository(DSLContext sql) {
		this.sql = sql;
	}

	public List<JobOffer> getAll() {
		Result<JobOffersRecord> all = sql.fetch(JOB_OFFERS);

		// logger.info("Returning job offers: \n" + all);

		if (all.isEmpty())
			return Collections.emptyList();

		return all.stream().map(JobOffer::new).collect(Collectors.toList());
	}

	public JobOffer save(@NotNull JobOffer jobOffer) {

		JobOffersRecord existingRecord = sql.fetchOne(JOB_OFFERS,
				JOB_OFFERS.JOB_OFFER_ID.eq(jobOffer.getJobOfferId()));

		if (existingRecord == null) {
			JobOffersRecord newRecord = sql.newRecord(JOB_OFFERS);

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

			jobOffer.setJobOfferId(newRecord.getJobOfferId());

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

	public List<JobOffer> getAllByOrganization(@NotNull Long organizationId) {

		Result<JobOffersRecord> all = sql.fetch(JOB_OFFERS,
				JOB_OFFERS.ORGANIZATION_ID.eq(organizationId));

		// logger.info("Returning job offers: \n" + all);

		if (all.isEmpty())
			return Collections.emptyList();

		return all.stream().map(JobOffer::new).collect(Collectors.toList());

	}

	public JobOffer get(Long jobOfferId) {
		return sql.fetchOne(JOB_OFFERS, JOB_OFFERS.JOB_OFFER_ID.eq(jobOfferId))
				.into(JobOffer.class);
	}

	public List<JobOffer> getSiteWideJobOffers() {

		Result<JobOffersRecord> result = sql.fetch(JOB_OFFERS,
				JOB_OFFERS.SITE_WIDE.eq(true));

		if (result.isEmpty())
			return Collections.emptyList();

		return result.stream().map(JobOffer::new).collect(Collectors.toList());
	}

}
