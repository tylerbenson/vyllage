package accounts.repository;

import static accounts.domain.tables.Lms.LMS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;
import oauth.model.LMSAccount;
import oauth.repository.LTIKeyRepository;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import user.common.Organization;
import accounts.domain.tables.records.LmsRecord;

import com.newrelic.api.agent.NewRelic;

@Repository
public class LMSRepository {

	private final Logger logger = Logger.getLogger(LMSRepository.class
			.getName());
	private final DSLContext sql;
	private final DataSourceTransactionManager txManager;
	private final LTIKeyRepository ltiKeyRepository;

	@Inject
	public LMSRepository(final DSLContext sql,
			final DataSourceTransactionManager txManager,
			final LTIKeyRepository ltiKeyRepository) {
		this.sql = sql;
		this.txManager = txManager;
		this.ltiKeyRepository = ltiKeyRepository;
	}

	public Long createLMSAccount(@NonNull LMSAccount lmsAccount) {
		Long lmsId = null;
		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());
		Object savepoint = transaction.createSavepoint();
		try {
			Organization organizationByExternalId = ltiKeyRepository
					.getOrganizationByConsumerKey(lmsAccount.getConsumerKey());

			LmsRecord newRecord = sql.newRecord(LMS);
			newRecord.setLmsGuid(lmsAccount.getLmsGuid());
			newRecord.setLmsName(lmsAccount.getLmsName());
			newRecord.setLmsVersion(lmsAccount.getLmsVersion());
			newRecord.setLtiVersion(lmsAccount.getLtiVersion());
			newRecord.setOauthVersion(lmsAccount.getOauthVersion());
			newRecord.setLmsTypeId(lmsAccount.getType().getTypeId());
			newRecord.setOrganizationId(organizationByExternalId
					.getOrganizationId());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			newRecord.store();
			lmsId = newRecord.getLmsId();

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);
		}
		return lmsId;
	}

	public Long get(@NonNull String lmsGuid) throws LMSNotFoundException {

		LmsRecord lmsAccountRecords = sql.fetchOne(LMS,
				LMS.LMS_GUID.eq(lmsGuid));
		if (lmsAccountRecords == null)
			throw new LMSNotFoundException("LMS with guid name '" + lmsGuid
					+ "' not found.");
		return lmsAccountRecords.getLmsId();
	}
}
