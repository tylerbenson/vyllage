package accounts.repository;

import static accounts.domain.tables.LmsKey.LMS_KEY;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;
import oauth.repository.LMSKey;
import oauth.repository.LMSKeyRepository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import user.common.Organization;
import user.common.User;
import accounts.domain.tables.records.LmsKeyRecord;

@Repository
public class LMSKeyRepositoryImpl implements LMSKeyRepository {

	private final Logger logger = Logger.getLogger(LMSKeyRepositoryImpl.class
			.getName());

	@Inject
	private DSLContext sql;

	@Override
	public Optional<LMSKey> get(@NonNull final String consumerKey) {

		final LmsKeyRecord keyRecord = sql.fetchOne(LMS_KEY,
				LMS_KEY.CONSUMER_KEY.eq(consumerKey));

		if (keyRecord == null) {
			logger.warning("LMS Key with consumer key: " + consumerKey
					+ " could not be found.");

			return Optional.empty();
		}

		final LMSKey lmsKey = getKey(keyRecord);

		logger.info(lmsKey.toString());

		return Optional.of(lmsKey);
	}

	/**
	 * Creates or saves a key searching by it's primary key (organization id).
	 */
	@Override
	public LMSKey save(@NonNull final User user,
			@NonNull final Organization organization,
			@NonNull final String consumerKey, @NonNull final String secret) {

		final LmsKeyRecord existingRecord = sql.fetchOne(LMS_KEY,
				LMS_KEY.KEY_ID.eq(organization.getOrganizationId()));

		final LMSKey lmsKey;

		if (existingRecord == null) {
			// create
			LmsKeyRecord newRecord = sql.newRecord(LMS_KEY);

			newRecord.setConsumerKey(consumerKey);
			newRecord.setSecret(secret);
			newRecord.setCreatorUserId(user.getUserId());
			newRecord.setModifiedByUserId(user.getUserId());

			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			newRecord.setKeyId(organization.getOrganizationId());

			newRecord.store();

			lmsKey = getKey(newRecord);

		} else {
			// update
			existingRecord.setModifiedByUserId(user.getUserId());
			existingRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));
			existingRecord.setSecret(secret);
			existingRecord.setKeyId(organization.getOrganizationId());
			existingRecord.setConsumerKey(consumerKey);

			existingRecord.store();

			lmsKey = getKey(existingRecord);
		}

		logger.info("User: " + user.getUsername() + " Created key: "
				+ lmsKey.toString());

		return lmsKey;
	}

	protected LMSKey getKey(@NonNull final LmsKeyRecord keyRecord) {
		final LMSKey lmsKey = new LMSKey(keyRecord.getConsumerKey(),
				keyRecord.getSecret());

		lmsKey.setKeyId(keyRecord.getKeyId());
		lmsKey.setCreatorUserId(keyRecord.getCreatorUserId());
		lmsKey.setModifiedByUserId(keyRecord.getModifiedByUserId());
		lmsKey.setLastModified(keyRecord.getLastModified().toLocalDateTime());
		lmsKey.setDateCreated(keyRecord.getDateCreated().toLocalDateTime());
		return lmsKey;
	}
}
