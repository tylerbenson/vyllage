package accounts.repository;

import static accounts.domain.tables.LmsKey.LMS_KEY;

import javax.inject.Inject;

import oauth.repository.LMSKey;
import oauth.repository.LMSKeyRepository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.LmsKeyRecord;

@Repository
public class LMSKeyRepositoryImpl implements LMSKeyRepository {

	@Inject
	private DSLContext sql;

	@Override
	public LMSKey get(final String consumerKey) {

		final LmsKeyRecord keyRecord = sql.fetchOne(LMS_KEY,
				LMS_KEY.CONSUMER_KEY.eq(consumerKey));

		final LMSKey lmsKey = new LMSKey(keyRecord.getConsumerKey(),
				keyRecord.getSecret());

		lmsKey.setKeyId(keyRecord.getKeyId());
		lmsKey.setCreatorUserId(keyRecord.getCreatorUserId());
		lmsKey.setModifiedByUserId(keyRecord.getModifiedByUserId());
		lmsKey.setOrganizationId(keyRecord.getOrganizationId());
		lmsKey.setLastModified(keyRecord.getLastModified().toLocalDateTime());
		lmsKey.setDateCreated(keyRecord.getDateCreated().toLocalDateTime());

		return lmsKey;
	}
}
