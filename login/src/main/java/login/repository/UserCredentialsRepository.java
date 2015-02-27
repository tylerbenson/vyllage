package login.repository;

import static login.domain.tables.UserCredentials.USER_CREDENTIALS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import login.domain.tables.records.UserCredentialsRecord;
import login.model.UserCredential;
import login.model.link.DocumentLink;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Handles links to documents. A user can generate links to share his documents
 * to other users.
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
				USER_CREDENTIALS.USERID.eq(userId).and(
						USER_CREDENTIALS.EXPIRES.isNull())).into(
				UserCredential.class);

		// logger.info("Loaded credentials " + userCredential);
		// Assert.notNull(userCredential.getUserId(),
		// "Loading credential failed! Userid is null!");
		userCredential.setUserId(userId);

		return userCredential;
	}

	/**
	 * Saves the user's password, does not expire.
	 * 
	 * @param userId
	 * @param password
	 */
	public void save(Long userId, String password) {
		Assert.notNull(userId);
		Assert.notNull(password);
		UserCredentialsRecord newRecord = sql.newRecord(USER_CREDENTIALS);
		newRecord.setPassword(getEncodedPassword(password));
		newRecord.setUserid(userId);
		newRecord.setEnabled(true);
		newRecord.setExpires(null);
		newRecord.insert();
	}

	/**
	 * Creates an expiring credential for document access.
	 * 
	 * @param linkRequest
	 * @param expires
	 */
	public void createDocumentLinkPassword(DocumentLink linkRequest,
			LocalDateTime expires) {
		UserCredentialsRecord newRecord = sql.newRecord(USER_CREDENTIALS);
		newRecord.setPassword(getEncodedPassword(linkRequest
				.getGeneratedPassword()));
		newRecord.setEnabled(true);
		newRecord.setUserid(linkRequest.getUserId());
		newRecord.setExpires(Timestamp.valueOf(expires));
		newRecord.insert();
	}

	/**
	 * Deletes the user credential used to login, etc.
	 * 
	 * @param userId
	 */
	public void delete(long userId) {
		sql.delete(USER_CREDENTIALS)
				.where(USER_CREDENTIALS.USERID.eq(userId).and(
						USER_CREDENTIALS.EXPIRES.isNull())).execute();
	}

	private String getEncodedPassword(String password) {
		return new BCryptPasswordEncoder().encode(password);
	}

	public boolean exists(Long userId, String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		for (UserCredentialsRecord record : sql.fetch(USER_CREDENTIALS,
				USER_CREDENTIALS.USERID.eq(userId))) {
			if (encoder.matches(password, record.getPassword()))
				return true;
		}

		return false;
	}
}
