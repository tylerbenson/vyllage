package accounts.repository;

import static accounts.domain.tables.AccountSetting.ACCOUNT_SETTING;
import static accounts.domain.tables.Emails.EMAILS;
import static accounts.domain.tables.UserCredentials.USER_CREDENTIALS;
import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.AccountSettingsEnum;
import accounts.model.account.settings.AvatarSourceEnum;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;

import com.newrelic.api.agent.NewRelic;

@Repository
public class BatchAccountCreationRepository {

	private final Logger logger = Logger
			.getLogger(BatchAccountCreationRepository.class.getName());

	private final DSLContext sql;

	private final DataSourceTransactionManager txManager;

	@Inject
	public BatchAccountCreationRepository(DSLContext sql,
			DataSourceTransactionManager txManager) {
		this.sql = sql;
		this.txManager = txManager;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createUsers(List<User> users, User loggedInUser,
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
}
