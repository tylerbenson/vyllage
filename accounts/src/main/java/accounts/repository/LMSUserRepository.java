package accounts.repository;

import static accounts.domain.tables.Users.USERS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;
import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.model.service.LMSUserDetailsService;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.lms.LMSUser;
import user.common.lms.LMSUserDetails;
import accounts.domain.tables.records.UsersRecord;
import accounts.model.Email;
import accounts.model.UserCredential;
import accounts.model.account.settings.AccountSetting;
import accounts.service.ConfirmationEmailService;

import com.newrelic.api.agent.NewRelic;

@Repository
public class LMSUserRepository implements LMSUserDetailsService {

	private final Logger logger = Logger.getLogger(LMSUserRepository.class
			.getName());
	private final DSLContext sql;
	private final UserOrganizationRoleRepository userOrganizationRoleRepository;
	private final UserCredentialsRepository credentialsRepository;
	private final AccountSettingRepository accountSettingRepository;
	private final DataSourceTransactionManager txManager;
	private final LMSRepository lmsRepository;
	private final LMSUserCredentialsRepository lmsUserCredentialsRepository;
	private final ConfirmationEmailService confirmationEmailService;

	@Inject
	public LMSUserRepository(
			final DSLContext sql,
			final UserOrganizationRoleRepository userOrganizationRoleRepository,
			final UserCredentialsRepository credentialsRepository,
			final AccountSettingRepository accountSettingRepository,
			final DataSourceTransactionManager txManager,
			final LMSRepository lmsRepository,
			final LMSUserCredentialsRepository lmsUserCredentialsRepository,
			final ConfirmationEmailService confirmationEmailService) {
		this.sql = sql;
		this.userOrganizationRoleRepository = userOrganizationRoleRepository;
		this.credentialsRepository = credentialsRepository;
		this.accountSettingRepository = accountSettingRepository;
		this.txManager = txManager;
		this.lmsRepository = lmsRepository;
		this.lmsUserCredentialsRepository = lmsUserCredentialsRepository;
		this.confirmationEmailService = confirmationEmailService;
	}

	public User get(Long userId) throws UserNotFoundException {
		UsersRecord record = sql.fetchOne(USERS, USERS.USER_ID.eq(userId));

		if (record == null)
			throw new UserNotFoundException("User with id '" + userId
					+ "' not found.");
		User user = getUserData(record);
		return user;
	}

	@Override
	public void createUser(@NonNull UserDetails userDetails,
			@NonNull LMSAccount lmsAccount, @NonNull LMSUser lmsUser) {
		User user = (User) userDetails;

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			UsersRecord newRecord = sql.newRecord(USERS);
			newRecord.setUserName(user.getUsername());
			newRecord.setFirstName(user.getFirstName());
			newRecord.setMiddleName(user.getMiddleName());
			newRecord.setLastName(user.getLastName());
			newRecord.setEnabled(user.isEnabled());
			newRecord.setResetPasswordOnNextLogin(false);
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));
			newRecord.store();
			Assert.notNull(newRecord.getUserId());

			user.setUserId(newRecord.getUserId());

			boolean defaultEmail = true;
			boolean confirmed = false;
			Email email = new Email(newRecord.getUserId(), user.getUsername(),
					defaultEmail, confirmed);

			this.confirmationEmailService.sendConfirmationEmail(user, email);

			credentialsRepository.create(newRecord.getUserId(),
					user.getPassword());

			for (GrantedAuthority role : user.getAuthorities()) {
				((UserOrganizationRole) role).setUserId(newRecord.getUserId());
				userOrganizationRoleRepository
						.create((UserOrganizationRole) role);
			}

			AccountSetting emailUpdatesSetting = AccountSetting
					.createEmailUpdatesSetting(newRecord.getUserId());

			this.accountSettingRepository.set(emailUpdatesSetting);

			// Add LMS details if doesn't exist
			Long lmsId = null;
			try {
				lmsId = lmsRepository.get(lmsAccount.getLmsGuid());
			} catch (LMSNotFoundException exception) {
				lmsId = lmsRepository.createLMSAccount(lmsAccount);
			}
			Assert.notNull(lmsId);

			// Check LMS user credentials exist
			boolean isUserExist = lmsUserCredentialsRepository.userExists(
					lmsUser.getUserId(), lmsId);

			if (!isUserExist) {
				// Create LMS user credentials
				lmsUserCredentialsRepository.createUser(lmsUser.getUserId(),
						newRecord.getUserId(), lmsId);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);

		}
	}

	/**
	 * Adds LMS details to an existing user.
	 * 
	 * @param user
	 * @param lmsRequest
	 */
	public void addLMSDetails(final User user, final LMSAccount lmsAccount,
			final LMSUser lmsUser) {
		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			Long lmsId = null;

			try {
				lmsId = lmsRepository.get(lmsAccount.getLmsGuid());
			} catch (LMSNotFoundException exception) {
				lmsId = lmsRepository.createLMSAccount(lmsAccount);
			}
			Assert.notNull(lmsId);

			// Check LMS user credentials exist
			boolean isUserExist = lmsUserCredentialsRepository.userExists(
					lmsUser.getUserId(), lmsId);

			if (!isUserExist) {
				// Create LMS user credentials
				Assert.notNull(user.getUserId());
				lmsUserCredentialsRepository.createUser(lmsUser.getUserId(),
						user.getUserId(), lmsId);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);

		}
	}

	/**
	 * Adds LMS details to an existing user.
	 * 
	 * @param user
	 * @param lmsRequest
	 */
	public void addLMSDetails(@NonNull User user, @NonNull LMSRequest lmsRequest) {

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			LMSAccount lmsAccount = lmsRequest.getLmsAccount();
			Long lmsId = null;

			try {
				lmsId = lmsRepository.get(lmsAccount.getLmsGuid());
			} catch (LMSNotFoundException exception) {
				lmsId = lmsRepository.createLMSAccount(lmsAccount);
			}
			Assert.notNull(lmsId);

			// Check LMS user credentials exist
			boolean isUserExist = lmsUserCredentialsRepository.userExists(
					lmsRequest.getLmsUser().getUserId(), lmsId);

			if (!isUserExist) {
				// Create LMS user credentials
				Assert.notNull(user.getUserId());
				lmsUserCredentialsRepository.createUser(lmsRequest.getLmsUser()
						.getUserId(), user.getUserId(), lmsId);
			}
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);

		}
	}

	@Override
	public boolean userExists(@NonNull String username) {
		return sql.fetchExists(sql.select().from(USERS)
				.where(USERS.USER_NAME.eq(username)));
	}

	@Override
	public User loadUserByUsername(@NonNull String username)
			throws UsernameNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USER_NAME.eq(username));

		if (record == null)
			throw new UsernameNotFoundException("User with username '"
					+ username + "' not found.");

		User user = getUserData(record);

		return user;
	}

	protected User getUserData(@NonNull UsersRecord record) {

		boolean accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true;
		List<UserOrganizationRole> roles = userOrganizationRoleRepository
				.getByUserId(record.getUserId());
		UserCredential credential = credentialsRepository.get(record
				.getUserId());

		User user = new User(record.getUserId(), record.getFirstName(),
				record.getMiddleName(), record.getLastName(),
				record.getUserName(), credential.getPassword(),
				record.getEnabled(), accountNonExpired, credentialsNonExpired,
				accountNonLocked, roles, record.getDateCreated()
						.toLocalDateTime(), record.getLastModified()
						.toLocalDateTime());
		return user;
	}

	@Override
	@Deprecated
	public LMSUserDetails loadUserByUserId(@NonNull String userId)
			throws UsernameNotFoundException, DataAccessException {
		return null;
	}

}
