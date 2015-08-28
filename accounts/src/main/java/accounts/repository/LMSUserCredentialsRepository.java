/**
 *
 */
package accounts.repository;

import static accounts.domain.tables.LmsUserCredentials.LMS_USER_CREDENTIALS;

import javax.inject.Inject;

import lombok.NonNull;

import org.jooq.DSLContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;

import user.common.LMSUserCredentials;
import accounts.domain.tables.records.LmsUserCredentialsRecord;

/**
 * @author kunal.shankar
 */

@Repository
public class LMSUserCredentialsRepository {

	private final DSLContext sql;
	private final DataSourceTransactionManager txManager;

	@Inject
	public LMSUserCredentialsRepository(final DSLContext sql,
			final DataSourceTransactionManager txManager) {
		super();
		this.sql = sql;
		this.txManager = txManager;
	}

	public LMSUserCredentials get(String lmsUserId)
			throws UserNotFoundException {

		LmsUserCredentialsRecord record = sql.fetchOne(LMS_USER_CREDENTIALS,
				LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId));

		if (record == null)
			throw new UserNotFoundException("User with id '" + lmsUserId
					+ "'not found.");

		LMSUserCredentials lmsUserCredentials = new LMSUserCredentials();
		lmsUserCredentials.setLmsId(lmsUserCredentials.getLmsId());
		lmsUserCredentials.setLmsUserId(lmsUserCredentials.getLmsUserId());
		lmsUserCredentials.setUserId(lmsUserCredentials.getUserId());
		return lmsUserCredentials;
	}

	public Long getUserId(String lmsUserId) throws UserNotFoundException {
		LmsUserCredentialsRecord record = sql.fetchOne(LMS_USER_CREDENTIALS,
				LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId));
		if (record == null)
			throw new UserNotFoundException("User with id '" + lmsUserId
					+ "'not found.");
		return record.getUserId();
	}

	public void createUser(@NonNull String lmsUserId, @NonNull Long userId,
			@NonNull Long lmsId) {
		LmsUserCredentialsRecord newRecord = sql
				.newRecord(LMS_USER_CREDENTIALS);
		newRecord.setLmsUserId(lmsUserId);
		newRecord.setUserId(userId);
		newRecord.setLmsId(lmsId);
		newRecord.setExpires(null);
		newRecord.insert();
	}

	public boolean userExists(String lmsUserId) {
		return sql.fetchExists(sql.select().from(LMS_USER_CREDENTIALS)
				.where(LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId)));
	}

	public boolean userExists(String lmsUserId, Long lmsId) {
		return sql.fetchExists(sql
				.select()
				.from(LMS_USER_CREDENTIALS)
				.where(LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId).and(
						LMS_USER_CREDENTIALS.LMS_ID.eq(lmsId))));
	}

}
