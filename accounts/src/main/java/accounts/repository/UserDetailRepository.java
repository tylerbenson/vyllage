package accounts.repository;

import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;
import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Userconnection.USERCONNECTION;
import static accounts.domain.tables.Users.USERS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.social.SocialUser;
import accounts.domain.tables.records.UsersRecord;
import accounts.model.UserCredential;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;

import com.newrelic.api.agent.NewRelic;

@Repository
public class UserDetailRepository implements UserDetailsManager,
		SocialUserDetailsService {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	// @Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DSLContext sql;

	@Autowired
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserCredentialsRepository credentialsRepository;

	@Autowired
	private AccountSettingRepository accountSettingRepository;

	@Autowired
	private DataSourceTransactionManager txManager;

	public UserDetailRepository() {
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
	public User loadUserByUsername(String username)
			throws UsernameNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USER_NAME.eq(username));

		if (record == null)
			throw new UsernameNotFoundException("User with username '"
					+ username + "' not found.");

		User user = getUserData(record);

		return user;
	}

	protected User getUserData(UsersRecord record) {
		Assert.notNull(record);

		// TODO: eventually we'll need these fields in the database.
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
	// @Transactional
	public void createUser(UserDetails userDetails) {
		Assert.notNull(userDetails);

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
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));
			newRecord.store();

			Assert.notNull(newRecord.getUserId());

			credentialsRepository.create(newRecord.getUserId(),
					user.getPassword());

			for (GrantedAuthority role : user.getAuthorities()) {
				((UserOrganizationRole) role).setUserId(newRecord.getUserId());
				userOrganizationRoleRepository
						.create((UserOrganizationRole) role);

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
			emailUpdatesSetting
					.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
			emailUpdatesSetting.setValue(EmailFrequencyUpdates.NEVER.name()
					.toLowerCase());
			accountSettingRepository.set(newRecord.getUserId(),
					emailUpdatesSetting);

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);

		}
	}

	/**
	 * Updates the user data. DOES NOT CHANGE USER PASSWORD.
	 */
	@Override
	// @Transactional
	public void updateUser(UserDetails userDetails) {
		Assert.notNull(userDetails);

		User user = (User) userDetails;
		Assert.notNull(user.getAuthorities());
		Assert.notEmpty(user.getAuthorities());

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {
			UsersRecord record = sql.fetchOne(USERS,
					USERS.USER_ID.eq(user.getUserId()));

			if (record == null)
				throw new UsernameNotFoundException("User with userName '"
						+ user.getUsername() + "' not found.");
			record.setUserName(user.getUsername());
			record.setEnabled(user.isEnabled());
			record.setLastModified(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			record.setFirstName(user.getFirstName());
			record.setMiddleName(user.getMiddleName());
			record.setLastName(user.getLastName());
			record.update();

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			txManager.commit(transaction);
		}
	}

	/**
	 * Disables user. Deletes credentials. Deletes account settings. Deletes
	 * User Roles.
	 * 
	 * 
	 * @param userId
	 */
	@Override
	// @Transactional
	public void deleteUser(String username) {
		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {
			UsersRecord record = sql.fetchOne(USERS,
					USERS.USER_NAME.eq(username));
			long userId = record.getUserId();

			userOrganizationRoleRepository.deleteByUserId(userId);
			credentialsRepository.deleteByUserId(userId);
			accountSettingRepository.deleteByUserId(userId);

			sql.update(USERS).set(USERS.ENABLED, false)
					.where(USERS.USER_ID.eq(userId)).execute();

			// user is not deleted
			// record.delete();

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			txManager.commit(transaction);
		}
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext()
				.getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context "
							+ "for current user.");
		}

		String username = currentUser.getName();
		// If an authentication manager has been set, re-authenticate the user
		// with the supplied password.
		if (authenticationManager != null) {
			logger.info("Reauthenticating user '" + username
					+ "' for password change request.");

			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(
							username, oldPassword));
		} else {
			logger.info("No authentication manager set. Password won't be re-checked.");
		}

		logger.info("Changing password for user '" + username + "'");

		Long userId = this.loadUserByUsername(username).getUserId();
		updateCredential(newPassword, userId);

		SecurityContextHolder.getContext().setAuthentication(
				createNewAuthentication(currentUser, newPassword));
	}

	/**
	 * When the user resets the password we don't have the previous one to
	 * compare against.
	 * 
	 * @param newPassword
	 */
	public void changePassword(String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext()
				.getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context "
							+ "for current user.");
		}

		String username = currentUser.getName();

		logger.info("Changing password for user '" + username + "'");

		Long userId = this.loadUserByUsername(username).getUserId();
		updateCredential(newPassword, userId);

		SecurityContextHolder.getContext().setAuthentication(
				createNewAuthentication(currentUser, newPassword));
	}

	public void updateCredential(String newPassword, Long userId) {
		UserCredential userCredential = credentialsRepository.get(userId);
		userCredential.setEnabled(true);
		userCredential.setPassword(newPassword);

		credentialsRepository.update(userCredential);
	}

	@Override
	public boolean userExists(String username) {
		return sql.fetchExists(sql.select().from(USERS)
				.where(USERS.USER_NAME.eq(username)));
	}

	public List<User> getAll() {

		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		return sql
				.fetch(USERS)
				.stream()
				.map((UsersRecord record) -> new User(record.getUserId(),
						record.getFirstName(), record.getMiddleName(), record
								.getLastName(), record.getUserName(),
						credentialsRepository.get(record.getUserId())
								.getPassword(), record.getEnabled(),
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, userOrganizationRoleRepository
								.getByUserId(record.getUserId()), record
								.getDateCreated().toLocalDateTime(), record
								.getLastModified().toLocalDateTime()))
				.collect(Collectors.toList());
	}

	public List<User> getAll(List<Long> userIds) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		return sql
				.fetch(USERS, USERS.USER_ID.in(userIds))
				.stream()
				.map((UsersRecord record) -> new User(record.getUserId(),
						record.getFirstName(), record.getMiddleName(), record
								.getLastName(), record.getUserName(),
						credentialsRepository.get(record.getUserId())
								.getPassword(), record.getEnabled(),
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, userOrganizationRoleRepository
								.getByUserId(record.getUserId()), record
								.getDateCreated().toLocalDateTime(), record
								.getLastModified().toLocalDateTime()))
				.collect(Collectors.toList());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	// @Transactional
	public void addUsers(List<User> users, User loggedInUser) {
		final boolean enabled = true;

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		List userBatch = users
				.stream()
				.map((User u) -> sql.insertInto(USERS, USERS.USER_NAME,
						USERS.ENABLED, USERS.DATE_CREATED, USERS.LAST_MODIFIED,
						USERS.FIRST_NAME, USERS.MIDDLE_NAME, USERS.LAST_NAME)
						.values(u.getUsername(),
								enabled,
								Timestamp.valueOf(LocalDateTime.now(ZoneId
										.of("UTC"))),
								Timestamp.valueOf(LocalDateTime.now(ZoneId
										.of("UTC"))), u.getFirstName(),
								u.getMiddleName(), u.getLastName())

				).collect(Collectors.toList());

		try {
			sql.batch(userBatch).execute();

			List otherInserts = new ArrayList();

			// hmm, well, we won't be inserting THAT many users...
			// there should be a better way
			users.stream().forEach(
					u -> u.setUserId(sql.select().from(USERS)
							.where(USERS.USER_NAME.eq(u.getUsername()))
							.fetchOne(USERS.USER_ID)));

			for (User user : users) {

				credentialsRepository.create(user.getUserId(),
						user.getPassword());

				for (GrantedAuthority authority : user.getAuthorities()) {
					otherInserts.add(sql.insertInto(USER_ORGANIZATION_ROLES,
							USER_ORGANIZATION_ROLES.USER_ID,
							USER_ORGANIZATION_ROLES.ROLE,
							USER_ORGANIZATION_ROLES.ORGANIZATION_ID,
							USER_ORGANIZATION_ROLES.DATE_CREATED,
							USER_ORGANIZATION_ROLES.AUDIT_USER_ID).values(
							user.getUserId(),
							authority.getAuthority(),
							((UserOrganizationRole) authority)
									.getOrganizationId(),
							Timestamp.valueOf(LocalDateTime.now(ZoneId
									.of("UTC"))), loggedInUser.getUserId()));

					// role setting
					otherInserts.add(sql.insertInto(ACCOUNT_SETTING,
							ACCOUNT_SETTING.USER_ID, ACCOUNT_SETTING.NAME,
							ACCOUNT_SETTING.VALUE, ACCOUNT_SETTING.PRIVACY)
							.values(user.getUserId(), "role",
									authority.getAuthority(),
									Privacy.PRIVATE.name().toLowerCase()));

					// organization setting
					otherInserts.add(sql.insertInto(ACCOUNT_SETTING,
							ACCOUNT_SETTING.USER_ID, ACCOUNT_SETTING.NAME,
							ACCOUNT_SETTING.VALUE, ACCOUNT_SETTING.PRIVACY)
							.values(user.getUserId(),
									"organization",
									organizationRepository.get(
											((UserOrganizationRole) authority)
													.getOrganizationId())
											.getOrganizationName(),
									Privacy.PRIVATE.name().toLowerCase()));

				}

				// create other user settings
				// email
				otherInserts.add(sql.insertInto(ACCOUNT_SETTING,
						ACCOUNT_SETTING.USER_ID, ACCOUNT_SETTING.NAME,
						ACCOUNT_SETTING.VALUE, ACCOUNT_SETTING.PRIVACY).values(
						user.getUserId(), "email", user.getUsername(),
						Privacy.PRIVATE.name().toLowerCase()));

				// email frequency updates
				otherInserts.add(sql.insertInto(ACCOUNT_SETTING,
						ACCOUNT_SETTING.USER_ID, ACCOUNT_SETTING.NAME,
						ACCOUNT_SETTING.VALUE, ACCOUNT_SETTING.PRIVACY).values(
						user.getUserId(), "emailUpdates",
						EmailFrequencyUpdates.NEVER.name().toLowerCase(),
						Privacy.PRIVATE.name().toLowerCase()));

			}

			sql.batch(otherInserts).execute();

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			txManager.commit(transaction);
		}
	}

	protected Authentication createNewAuthentication(
			Authentication currentAuth, String newPassword) {
		UserDetails user = loadUserByUsername(currentAuth.getName());

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());
		newAuthentication.setDetails(currentAuth.getDetails());

		return newAuthentication;
	}

	public List<AccountNames> getNames(List<Long> userIds) {
		return sql
				.select(USERS.USER_ID, USERS.FIRST_NAME, USERS.MIDDLE_NAME,
						USERS.LAST_NAME)
				.from(USERS)
				.where(USERS.USER_ID.in(userIds))
				.fetch()
				.stream()
				.map(r -> new AccountNames(r.getValue(USERS.USER_ID), r
						.getValue(USERS.FIRST_NAME), r
						.getValue(USERS.MIDDLE_NAME), r
						.getValue(USERS.LAST_NAME)))
				.collect(Collectors.toList());
	}

	@Override
	public SocialUserDetails loadUserByUserId(String userId)
			throws UsernameNotFoundException, DataAccessException {
		return new SocialUser(this.loadUserByUsername(userId));
	}

	public boolean enableDisableUser(Long userId) {
		Result<Record1<Boolean>> result = sql.select(USERS.ENABLED).from(USERS)
				.where(USERS.USER_ID.eq(userId)).fetch();
		Boolean enabled = result.get(0).getValue(USERS.ENABLED);

		logger.info("userId " + userId + " status: " + enabled);

		sql.update(USERS).set(USERS.ENABLED, !enabled)
				.where(USERS.USER_ID.eq(userId)).execute();

		return !enabled;
	}

	public void changeEmail(User user, String email) {
		sql.update(USERS).set(USERS.USER_NAME, email)
				.where(USERS.USER_ID.eq(user.getUserId())).execute();

		sql.update(ACCOUNT_SETTING)
				.set(ACCOUNT_SETTING.VALUE, email)
				.where(ACCOUNT_SETTING.USER_ID.eq(user.getUserId()).and(
						ACCOUNT_SETTING.NAME.eq("email"))).execute();

		sql.update(USERCONNECTION).set(USERCONNECTION.USERID, email)
				.where(USERCONNECTION.USERID.eq(user.getUsername())).execute();
	}

}
