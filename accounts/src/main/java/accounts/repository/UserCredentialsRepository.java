package accounts.repository;

import static accounts.domain.tables.UserCredentials.USER_CREDENTIALS;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import accounts.domain.tables.records.UserCredentialsRecord;
import accounts.model.UserCredential;

/**
 * Handles user's passwords.
 * 
 * @author uh
 *
 */
@Repository
public class UserCredentialsRepository {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(UserCredentialsRepository.class.getName());

	@Autowired
	private DSLContext sql;

	/**
	 * Returns the permanent user credential, the one used to login, etc.
	 * 
	 * @param id
	 * @return
	 */
	public UserCredential get(Long userId) {
		Assert.notNull(userId);

		UserCredential userCredential = sql.fetchOne(
				USER_CREDENTIALS,
				USER_CREDENTIALS.USER_ID.eq(userId).and(
						USER_CREDENTIALS.EXPIRES.isNull())).into(
				UserCredential.class);

		userCredential.setUserId(userId);

		return userCredential;
	}

	/**
	 * Saves the user's password, does not expire.
	 * 
	 * @param userId
	 * @param password
	 */
	public void create(Long userId, String password) {
		Assert.notNull(userId);
		Assert.notNull(password);
		UserCredentialsRecord newRecord = sql.newRecord(USER_CREDENTIALS);
		newRecord.setPassword(getEncodedPassword(password));
		newRecord.setUserId(userId);
		newRecord.setEnabled(true);
		newRecord.setExpires(null);
		newRecord.insert();
	}

	/**
	 * Deactivates the user credential used to login, etc.
	 * 
	 * @param userId
	 */
	public void deleteByUserId(long userId) {
		sql.update(USER_CREDENTIALS)
				.set(USER_CREDENTIALS.ENABLED, false)
				.where(USER_CREDENTIALS.USER_ID.eq(userId).and(
						USER_CREDENTIALS.EXPIRES.isNull())).execute();
	}

	private String getEncodedPassword(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}

	/**
	 * Determines if the given password is active.
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean isActive(Long userId, String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		for (UserCredentialsRecord record : sql.fetch(
				USER_CREDENTIALS,
				USER_CREDENTIALS.USER_ID.eq(userId)
						.and(USER_CREDENTIALS.ENABLED.isTrue())
						.and(USER_CREDENTIALS.EXPIRES.isNotNull()))) {

			// the password is active if the encoder matches and the
			// current date is before the expiration date.
			if (encoder.matches(password, record.getPassword())
					&& LocalDateTime.now().isBefore(
							record.getExpires().toLocalDateTime())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Updates the credentials the users uses to login, ignores the ones for
	 * document links.
	 * 
	 * @param userCredential
	 */
	public void update(UserCredential userCredential) {
		sql.update(USER_CREDENTIALS)
				.set(USER_CREDENTIALS.PASSWORD,
						getEncodedPassword(userCredential.getPassword()))
				.set(USER_CREDENTIALS.ENABLED, userCredential.isEnabled())
				.where(USER_CREDENTIALS.USER_ID.eq(userCredential.getUserId())
						.and(USER_CREDENTIALS.EXPIRES.isNull())).execute();

	}
}
