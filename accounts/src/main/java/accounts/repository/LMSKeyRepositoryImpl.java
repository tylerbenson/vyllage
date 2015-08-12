package accounts.repository;

import static accounts.domain.tables.LmsKey.LMS_KEY;

import javax.inject.Inject;

import oauth.repository.LMSKey;
import oauth.repository.LMSKeyRepository;
import oauth.utilities.LMSConstants;

import org.jooq.DSLContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import accounts.domain.tables.records.LmsKeyRecord;

@Repository
public class LMSKeyRepositoryImpl implements LMSKeyRepository {

	@Inject
	private DSLContext sql;

	@Inject
	private Environment environment;

	@Override
	public LMSKey get(final String consumerKey) {

		final LmsKeyRecord keyRecord = sql.fetchOne(LMS_KEY,
				LMS_KEY.CONSUMER_KEY.eq(consumerKey));

		final String oauthKey = environment.getProperty(LMSConstants.OAUTH_KEY);
		final String oauthSecret = environment
				.getProperty(LMSConstants.OAUTH_SECRET);

		Assert.notNull(oauthKey);
		Assert.notNull(oauthSecret);

		Assert.isTrue(!oauthKey.isEmpty());
		Assert.isTrue(!oauthSecret.isEmpty());

		final LMSKey lmsKey = new LMSKey(oauthKey, oauthSecret);

		lmsKey.setKeyId(keyRecord.getKeyId());

		return lmsKey;
	}
}
