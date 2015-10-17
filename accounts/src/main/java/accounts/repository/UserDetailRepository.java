package accounts.repository;

import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;
import static accounts.domain.tables.Emails.EMAILS;
import static accounts.domain.tables.UserCredentials.USER_CREDENTIALS;
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

import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
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
import user.common.constants.AccountSettingsEnum;
import user.common.constants.RolesEnum;
import user.common.social.SocialUser;
import accounts.domain.tables.records.UsersRecord;
import accounts.model.Email;
import accounts.model.UserCredential;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.AvatarSourceEnum;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;
import accounts.service.ConfirmationEmailService;

import com.newrelic.api.agent.NewRelic;

@Repository
public class UserDetailRepository implements UserDetailsManager,
		SocialUserDetailsService {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	// @Autowired
	private AuthenticationManager authenticationManager;

	private final DSLContext sql;

	private final UserOrganizationRoleRepository userOrganizationRoleRepository;

	private final UserCredentialsRepository credentialsRepository;

	private final AccountSettingRepository accountSettingRepository;

	private final DataSourceTransactionManager txManager;

	// Services should not be called from repositories but I don't see any other
	// way right now, maybe handling this kind of logic with events?
	private ConfirmationEmailService confirmationEmailService;

	@Inject
	public UserDetailRepository(
			DSLContext sql,
			final UserOrganizationRoleRepository userOrganizationRoleRepository,
			final UserCredentialsRepository credentialsRepository,
			final AccountSettingRepository accountSettingRepository,
			final DataSourceTransactionManager txManager,
			final ConfirmationEmailService confirmationEmailService) {
		this.sql = sql;
		this.userOrganizationRoleRepository = userOrganizationRoleRepository;
		this.credentialsRepository = credentialsRepository;
		this.accountSettingRepository = accountSettingRepository;
		this.txManager = txManager;
		this.confirmationEmailService = confirmationEmailService;
	}

	/**
	 * @param userId
	 * @return
	 * @throws UserNotFoundException
	 */
	public User get(Long userId) throws UserNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USER_ID.eq(userId));

		if (record == null)
			throw new UserNotFoundException("User with id '" + userId
					+ "' not found.");
		User user = this.getUserData(record);

		if (record.getResetPasswordOnNextLogin())
			throw new PasswordResetWasForcedException(
					"User logged  in for the first time: "
							+ record.getUserName(), user);

		return user;

	}

	@Override
	public User loadUserByUsername(String username)
			throws UsernameNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USER_NAME.eq(username));

		if (record == null)
			throw new UsernameNotFoundException("User with username '"
					+ username + "' not found.");

		User user = this.getUserData(record);

		if (record.getResetPasswordOnNextLogin())
			throw new PasswordResetWasForcedException(
					"User logged  in for the first time: "
							+ record.getUserName(), user);

		return user;
	}

	/**
	 * Gets the user without checking if the user has to change his password.
	 * 
	 * @param username
	 * @return
	 */
	private User getUser(String username) {
		UsersRecord record = sql.fetchOne(USERS, USERS.USER_NAME.eq(username));

		if (record == null)
			throw new UsernameNotFoundException("User with username '"
					+ username + "' not found.");

		return getUserData(record);
	}

	protected User getUserData(@NonNull UsersRecord record) {

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

	/**
	 * Creates a new user, optionally forces him to reset his password on the
	 * next login and sends confirmation email to verify the email address.
	 * 
	 * @param userDetails
	 * @param forcePasswordChange
	 * @param sendConfirmationEmail
	 * @return
	 */
	public User createUser(final @NonNull UserDetails userDetails,
			final boolean forcePasswordChange,
			final boolean sendConfirmationEmail) {
		this.createUser(userDetails);
		this.setForcePasswordReset(userDetails.getUsername(),
				forcePasswordChange);

		User user = getUser(userDetails.getUsername());

		if (sendConfirmationEmail) {

			boolean defaultEmail = true;
			boolean confirmed = false;
			Email email = new Email(user.getUserId(), user.getUsername(),
					defaultEmail, confirmed);

			this.confirmationEmailService.sendConfirmationEmail(user, email);
		}

		return user;
	}

	/**
	 * Create a new user with the supplied details. Use the other
	 * {@link #createUser(UserDetails, boolean, boolean) createUser} method
	 * instead.
	 */
	@Override
	@Deprecated()
	public void createUser(@NonNull UserDetails userDetails) {

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
			newRecord.setResetPasswordOnNextLogin(true);
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime
					.now(ZoneId.of("UTC"))));
			newRecord.store();

			Assert.notNull(newRecord.getUserId());
			user.setUserId(newRecord.getUserId());

			credentialsRepository.create(newRecord.getUserId(),
					user.getPassword());

			for (GrantedAuthority role : user.getAuthorities()) {
				((UserOrganizationRole) role).setUserId(newRecord.getUserId());
				userOrganizationRoleRepository
						.create((UserOrganizationRole) role);

			}

			AccountSetting emailUpdatesSetting = AccountSetting
					.createEmailUpdatesSetting(newRecord.getUserId());

			accountSettingRepository.set(emailUpdatesSetting);

			AccountSetting avatarSetting = AccountSetting
					.createGravatarAvatarSetting(newRecord.getUserId());

			accountSettingRepository.set(avatarSetting);

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
	public void updateUser(@NonNull UserDetails userDetails) {

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

	/**
	 * Changes password upon first login
	 *
	 * @param newPassword
	 */
	public void forcedPasswordChange(final Long userId, final String userName,
			final String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext()
				.getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context "
							+ "for current user.");
		}

		logger.info("Changing password for first login user '" + userName + "'");

		this.setForcePasswordReset(userName, false);

		this.updateCredential(newPassword, userId);

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
	public void addUsers(List<User> users, User loggedInUser,
			boolean forcePasswordChange) {
		final boolean enabled = true;

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		List userBatch = users
				.stream()
				.map((User u) -> sql.insertInto(USERS, USERS.USER_NAME,
						USERS.ENABLED, USERS.DATE_CREATED, USERS.LAST_MODIFIED,
						USERS.FIRST_NAME, USERS.MIDDLE_NAME, USERS.LAST_NAME,
						USERS.RESET_PASSWORD_ON_NEXT_LOGIN).values(
						u.getUsername(), enabled,
						Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))),
						Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))),
						u.getFirstName(), u.getMiddleName(), u.getLastName(),
						forcePasswordChange)

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

				otherInserts.add(sql.insertInto(USER_CREDENTIALS,
						USER_CREDENTIALS.ENABLED, USER_CREDENTIALS.EXPIRES,
						USER_CREDENTIALS.PASSWORD, USER_CREDENTIALS.USER_ID)
						.values(true, null, user.getPassword(),
								user.getUserId()));

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

				}

				// email frequency updates
				otherInserts.add(sql.insertInto(ACCOUNT_SETTING,
						ACCOUNT_SETTING.USER_ID, ACCOUNT_SETTING.NAME,
						ACCOUNT_SETTING.VALUE, ACCOUNT_SETTING.PRIVACY).values(
						user.getUserId(),
						AccountSettingsEnum.emailUpdates.name(),
						EmailFrequencyUpdates.NEVER.name().toLowerCase(),
						Privacy.PRIVATE.name().toLowerCase()));

				// default avatar settings
				otherInserts.add(sql.insertInto(ACCOUNT_SETTING,
						ACCOUNT_SETTING.USER_ID, ACCOUNT_SETTING.NAME,
						ACCOUNT_SETTING.VALUE, ACCOUNT_SETTING.PRIVACY).values(
						user.getUserId(), AccountSettingsEnum.avatar.name(),
						AvatarSourceEnum.GRAVATAR.name().toLowerCase(),
						Privacy.PRIVATE.name().toLowerCase()));

				// Emails table.
				// Email will be confirmed once the user logins.
				otherInserts.add(sql.insertInto(EMAILS, EMAILS.CONFIRMED,
						EMAILS.DATE_CREATED, EMAILS.DEFAULT_EMAIL,
						EMAILS.EMAIL, EMAILS.LAST_MODIFIED, EMAILS.USER_ID)
						.values(false,
								Timestamp.valueOf(LocalDateTime.now(ZoneId
										.of("UTC"))),
								true,
								user.getUsername(),
								Timestamp.valueOf(LocalDateTime.now(ZoneId
										.of("UTC"))), user.getUserId()));

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
				.and(USERS.ENABLED.eq(true))
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

	public void changeEmail(final User user, final @NonNull String newEmail) {
		Assert.isTrue(!newEmail.isEmpty());

		final String oldEmail = user.getUsername();

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			sql.update(USERS).set(USERS.USER_NAME, newEmail)
					.where(USERS.USER_ID.eq(user.getUserId())).execute();

			// delete this, we don't need it anymore
			accountSettingRepository.deleteByName(user.getUserId(),
					AccountSettingsEnum.newEmail.name());

			// if we are here then he confirmed the new email.

			StringBuilder sb = new StringBuilder();
			sb.append("User ").append(user) //
					.append(" confirms username/email change from '")//
					.append(oldEmail)//
					.append("' to '")//
					.append(newEmail)//
					.append("'."); //

			logger.info(sb.toString());

			// maybe it should be the other way around? The service calling this
			// repository?
			this.confirmationEmailService
					.confirmEmailChange(oldEmail, newEmail);

			sql.update(USERCONNECTION).set(USERCONNECTION.USERID, newEmail)
					.where(USERCONNECTION.USERID.eq(user.getUsername()))
					.execute();

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			txManager.commit(transaction);
		}
	}

	protected void setForcePasswordReset(String userName, boolean value) {
		sql.update(USERS).set(USERS.RESET_PASSWORD_ON_NEXT_LOGIN, value)
				.where(USERS.USER_NAME.eq(userName)).execute();
	}

	/**
	 * Returns whether a given user has any of the roles.
	 * 
	 * @param userId
	 * @param roles
	 * @return true, has any of the roles. | false, doesn't have any of the
	 *         roles
	 */
	public boolean userHasRoles(Long userId, List<RolesEnum> roles) {

		List<String> rolesAsStrings = roles.stream().map(r -> r.name())
				.collect(Collectors.toList());

		return sql.fetchExists(sql
				.select()
				.from(USER_ORGANIZATION_ROLES)
				.where(USER_ORGANIZATION_ROLES.USER_ID.eq(userId).and(
						USER_ORGANIZATION_ROLES.ROLE.in(rolesAsStrings))));
	}

}
