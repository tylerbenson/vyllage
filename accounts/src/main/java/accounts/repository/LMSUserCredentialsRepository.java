/**
 * 
 */
package accounts.repository;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import com.newrelic.api.agent.NewRelic;

import static accounts.domain.tables.LmsUserCredentials.LMS_USER_CREDENTIALS;
import accounts.domain.tables.records.LmsUserCredentialsRecord;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.inject.Inject;

import accounts.model.UserCredential;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;
import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import user.common.LMSUserCredentials;
import user.common.User;
import user.common.UserOrganizationRole;

/**
 * @author kunal.shankar
 *
 */

@Repository
public class LMSUserCredentialsRepository {

	private final DSLContext sql;
	private final DataSourceTransactionManager txManager;

	@Inject
	public LMSUserCredentialsRepository(final DSLContext sql, final DataSourceTransactionManager txManager) {
		this.sql = sql;
		this.txManager = txManager;
	}

	public LMSUserCredentials get(String lmsUserId) throws UserNotFoundException {
		LmsUserCredentialsRecord record = sql.fetchOne(LMS_USER_CREDENTIALS,
				LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId));

		if (record == null)
			throw new UserNotFoundException("User with id '" + lmsUserId + "'not found.");

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
			throw new UserNotFoundException("User with id '" + lmsUserId + "'not found.");
		return record.getUserId();
	}

	public void createUser(String lmsUserId, Long userId, Long lmsId, String password) {
		Assert.notNull(lmsUserId);
		Assert.notNull(userId);
		Assert.notNull(password);
		LmsUserCredentialsRecord newRecord = sql.newRecord(LMS_USER_CREDENTIALS);
		newRecord.setLmsUserId(lmsUserId);
		newRecord.setUserId(userId);
		newRecord.setLmsId(lmsId);
		newRecord.setPassword(password);
		newRecord.setExpires(null);
		newRecord.insert();
	}

	public boolean userExists(String lmsUserId) {
		return sql.fetchExists(
				sql.select().from(LMS_USER_CREDENTIALS).where(LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId)));
	}

	public boolean userExists(String lmsUserId, Long lmsId) {
		return sql.fetchExists(sql.select().from(LMS_USER_CREDENTIALS)
				.where(LMS_USER_CREDENTIALS.LMS_USER_ID.eq(lmsUserId).and(LMS_USER_CREDENTIALS.LMS_ID.eq(lmsId))));
	}

}
