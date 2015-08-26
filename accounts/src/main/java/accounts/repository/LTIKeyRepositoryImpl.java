package accounts.repository;

import static accounts.domain.tables.LtiCredentials.LTI_CREDENTIALS;
import static accounts.domain.tables.Organizations.ORGANIZATIONS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;
import oauth.repository.LTIKey;
import oauth.repository.LTIKeyRepository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import user.common.Organization;
import user.common.User;
import accounts.domain.tables.LtiCredentials;
import accounts.domain.tables.Organizations;
import accounts.domain.tables.records.LtiCredentialsRecord;

@Repository
public class LTIKeyRepositoryImpl implements LTIKeyRepository {

	private final Logger logger = Logger.getLogger(LTIKeyRepositoryImpl.class
			.getName());

	@Inject
	private DSLContext sql;

	@Override
	public Optional<LTIKey> get(@NonNull final String consumerKey) {

		final LtiCredentialsRecord keyRecord = sql.fetchOne(LTI_CREDENTIALS,
				LTI_CREDENTIALS.CONSUMER_KEY.eq(consumerKey));

		if (keyRecord == null) {
			logger.warning("LMS Key with consumer key: " + consumerKey
					+ " could not be found.");

			return Optional.empty();
		}

		final LTIKey lmsKey = getKey(keyRecord);

		logger.info(lmsKey.toString());

		return Optional.of(lmsKey);
	}

	/**
	 * Creates or saves a key searching by it's primary key (organization id).
	 */
	@Override
	public LTIKey save(@NonNull final User user,
			@NonNull final Organization organization,
			@NonNull final String consumerKey, @NonNull final String secret,
			final String externalOrganizationId) {

		final LtiCredentialsRecord existingRecord = sql.fetchOne(
				LTI_CREDENTIALS,
				LTI_CREDENTIALS.KEY_ID.eq(organization.getOrganizationId()));

		final LTIKey lmsKey;

		if (existingRecord == null) {
			// create
			LtiCredentialsRecord newRecord = sql.newRecord(LTI_CREDENTIALS);

			newRecord.setConsumerKey(consumerKey);
			newRecord.setSecret(secret);
			newRecord.setCreatorUserId(user.getUserId());
			newRecord.setModifiedByUserId(user.getUserId());

			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));

			newRecord.setKeyId(organization.getOrganizationId());
			newRecord.setExternalOrganizationId(externalOrganizationId);

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
			existingRecord.setExternalOrganizationId(externalOrganizationId);

			existingRecord.store();

			lmsKey = getKey(existingRecord);
		}

		logger.info("User: " + user.getUsername() + " Created key: "
				+ lmsKey.toString());

		return lmsKey;
	}

	@Override
	public Organization getOrganizationByConsumerKey(String consumerKey) {

		Organizations o = ORGANIZATIONS.as("o");
		LtiCredentials lti = LTI_CREDENTIALS.as("lti");

		// since the primary key is the organization id there can only be one
		List<Organization> list = sql.select(o.fields()).from(o).join(lti)
				.on(o.ORGANIZATION_ID.eq(lti.KEY_ID))
				.where(lti.CONSUMER_KEY.eq(consumerKey))
				.fetchInto(Organization.class);

		return list.get(0);
	}

	/**
	 * Returns the id user that created the key.
	 * 
	 * @param consumerKey
	 * @return admin id
	 */
	@Override
	public Long getAuditUser(String consumerKey) {

		final LtiCredentialsRecord existingRecord = sql.fetchOne(
				LTI_CREDENTIALS, LTI_CREDENTIALS.CONSUMER_KEY.eq(consumerKey));

		return existingRecord.getCreatorUserId();
	}

	protected LTIKey getKey(@NonNull final LtiCredentialsRecord keyRecord) {

		final LTIKey lmsKey = new LTIKey(keyRecord.getConsumerKey(),
				keyRecord.getSecret());

		lmsKey.setKeyId(keyRecord.getKeyId());
		lmsKey.setCreatorUserId(keyRecord.getCreatorUserId());
		lmsKey.setModifiedByUserId(keyRecord.getModifiedByUserId());
		lmsKey.setLastModified(keyRecord.getLastModified().toLocalDateTime());
		lmsKey.setDateCreated(keyRecord.getDateCreated().toLocalDateTime());
		lmsKey.setExternalOrganizationId(keyRecord.getExternalOrganizationId());
		return lmsKey;
	}
}
