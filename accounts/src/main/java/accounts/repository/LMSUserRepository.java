package accounts.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import com.newrelic.api.agent.NewRelic;

import accounts.model.UserCredential;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;
import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.model.service.LMSUserDetailsService;
import user.common.LMSUserCredentials;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.lms.LMSUserDetails;
import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;
import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;
import accounts.domain.tables.records.UsersRecord;

@Repository
public class LMSUserRepository implements LMSUserDetailsService {

	private final Logger logger = Logger.getLogger(LMSUserRepository.class.getName());
	private final DSLContext sql;
	private final UserOrganizationRoleRepository userOrganizationRoleRepository;
	private final OrganizationRepository organizationRepository;
	private final UserCredentialsRepository credentialsRepository;
	private final AccountSettingRepository accountSettingRepository;
	private final DataSourceTransactionManager txManager;
	private final LMSRepository lmsRepository;
	private final LMSUserCredentialsRepository lmsUserCredentialsRepository;

	@Inject
	public LMSUserRepository(final DSLContext sql, final UserOrganizationRoleRepository userOrganizationRoleRepository,
			final OrganizationRepository organizationRepository, final UserCredentialsRepository credentialsRepository,
			final AccountSettingRepository accountSettingRepository, final DataSourceTransactionManager txManager,
			final LMSRepository lmsRepository, final LMSUserCredentialsRepository lmsUserCredentialsRepository) {
		this.sql = sql;
		this.userOrganizationRoleRepository = userOrganizationRoleRepository;
		this.organizationRepository = organizationRepository;
		this.credentialsRepository = credentialsRepository;
		this.accountSettingRepository = accountSettingRepository;
		this.txManager = txManager;
		this.lmsRepository = lmsRepository;
		this.lmsUserCredentialsRepository = lmsUserCredentialsRepository;
	}

	public User get(Long userId) throws UserNotFoundException {
		UsersRecord record = sql.fetchOne(USERS, USERS.USER_ID.eq(userId));

		if (record == null)
			throw new UserNotFoundException("User with id '" + userId + "' not found.");
		User user = getUserData(record);
		return user;
	}

	@Override
	public void createUser(UserDetails userDetails, LMSRequest lmsRequest) {
		User user = (User) userDetails;

		TransactionStatus transaction = txManager.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			UsersRecord newRecord = sql.newRecord(USERS);
			newRecord.setUserName(user.getUsername());
			newRecord.setFirstName(user.getFirstName());
			newRecord.setMiddleName(user.getMiddleName());
			newRecord.setLastName(user.getLastName());
			newRecord.setEnabled(user.isEnabled());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
			newRecord.store();
			Assert.notNull(newRecord.getUserId());

			credentialsRepository.create(newRecord.getUserId(), user.getPassword());

			for (GrantedAuthority role : user.getAuthorities()) {
				((UserOrganizationRole) role).setUserId(newRecord.getUserId());
				userOrganizationRoleRepository.create((UserOrganizationRole) role);
			}

			AccountSetting emailSetting = new AccountSetting();
			emailSetting.setName("email");
			emailSetting.setUserId(newRecord.getUserId());
			emailSetting.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
			emailSetting.setValue(user.getUsername());
			accountSettingRepository.set(newRecord.getUserId(), emailSetting);

			AccountSetting emailUpdatesSetting = new AccountSetting();
			emailUpdatesSetting.setName("emailUpdates");
			emailUpdatesSetting.setUserId(newRecord.getUserId());
			emailUpdatesSetting.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
			emailUpdatesSetting.setValue(EmailFrequencyUpdates.NEVER.name().toLowerCase());
			accountSettingRepository.set(newRecord.getUserId(), emailUpdatesSetting);

			// Add LMS details if doesn't exist
			LMSAccount lmsAccount = lmsRequest.getLmsAccount();
			Long lmsId = null;
			try {
				lmsId = lmsRepository.get(lmsAccount.getLmsGuid());
			} catch (LMSNotFoundException exception) {
				lmsId = lmsRepository.createLMSAccount(lmsAccount);
			}
			Assert.notNull(lmsId);

			// Add LMS user credentials if doesn't exist
			boolean isUserExist = lmsUserCredentialsRepository.userExists(lmsRequest.getLmsUser().getUserId(), lmsId);
			lmsUserCredentialsRepository.createUser(lmsRequest.getLmsUser().getUserId(), newRecord.getUserId(), lmsId,
					user.getPassword());
			isUserExist = lmsUserCredentialsRepository.userExists(lmsRequest.getLmsUser().getUserId(), lmsId);

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);

		}
	}

	@Override
	public boolean userExists(String username) {
		return sql.fetchExists(sql.select().from(USERS).where(USERS.USER_NAME.eq(username)));
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USER_NAME.eq(username));

		if (record == null)
			throw new UsernameNotFoundException("User with username '" + username + "' not found.");

		User user = getUserData(record);

		return user;
	}

	protected User getUserData(UsersRecord record) {

		// TODO: eventually we'll need these fields in the database.
		boolean accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true;

		List<UserOrganizationRole> roles = userOrganizationRoleRepository.getByUserId(record.getUserId());

		UserCredential credential = credentialsRepository.get(record.getUserId());

		User user = new User(record.getUserId(), record.getFirstName(), record.getMiddleName(), record.getLastName(),
				record.getUserName(), credential.getPassword(), record.getEnabled(), accountNonExpired,
				credentialsNonExpired, accountNonLocked, roles, record.getDateCreated().toLocalDateTime(),
				record.getLastModified().toLocalDateTime());
		return user;
	}

	@Override
	public LMSUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUser(UserDetails user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}
}
