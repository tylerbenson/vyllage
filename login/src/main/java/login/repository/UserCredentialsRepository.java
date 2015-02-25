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

/**
 * Handles links to documents. A user can generate links to share his documents
 * to other users.
 * 
 * @author uh
 *
 */
@Repository
public class UserCredentialsRepository {

	private final Logger logger = Logger
			.getLogger(UserCredentialsRepository.class.getName());

	@Autowired
	private DSLContext sql;

	/**
	 * Returns the permanent user credential, the one use to login, etc.
	 * 
	 * @param id
	 * @return
	 */
	public UserCredential get(Long userId) {
		return sql.fetchOne(
				USER_CREDENTIALS,
				USER_CREDENTIALS.USERID.eq(userId).and(
						USER_CREDENTIALS.EXPIRES.isNull())).into(
				UserCredential.class);
	}

	/**
	 * Saves the user's password, does not expire.
	 * 
	 * @param userId
	 * @param password
	 */
	public void save(Long userId, String password) {
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
		logger.info("Saving link credentials.");

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

}
