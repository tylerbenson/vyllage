package documents.repository;

import static documents.domain.tables.JobOffers.JOB_OFFERS;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import documents.domain.tables.records.JobOffersRecord;
import documents.model.jobs.JobOffer;

@Repository
public class JobOffersRepository {
	private final Logger logger = Logger.getLogger(JobOffersRepository.class
			.getName());

	private DSLContext sql;

	@Inject
	public JobOffersRepository(DSLContext sql) {
		this.sql = sql;
	}

	public List<JobOffer> getAll() {
		Result<JobOffersRecord> all = sql.fetch(JOB_OFFERS);

		logger.info("Getting job offers: " + all);

		return all.stream().map(JobOffer::new).collect(Collectors.toList());
	}

}
