package accounts.repository;

import static accounts.domain.tables.OrganizationMembers.ORGANIZATION_MEMBERS;
import static accounts.domain.tables.Organizations.ORGANIZATIONS;
import static accounts.domain.tables.Roles.ROLES;
import static accounts.domain.tables.Users.USERS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import accounts.domain.tables.OrganizationMembers;
import accounts.domain.tables.Organizations;
import accounts.domain.tables.Roles;
import accounts.domain.tables.Users;
import accounts.domain.tables.records.UsersRecord;
import accounts.model.Organization;
import accounts.model.OrganizationMember;
import accounts.model.Role;
import accounts.model.User;
import accounts.model.UserCredential;
import accounts.model.UserFilterRequest;
import accounts.model.account.AccountNames;

@Repository
public class UserDetailRepository implements UserDetailsManager {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	// @Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DSLContext sql;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private OrganizationMemberRepository organizationMemberRepository;

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

		// TODO: eventually we'll need these fields in the database.
		boolean accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true;

		List<Role> roles = roleRepository.getByUserName(record.getUserName());

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
	public void createUser(UserDetails user) {

		Collection<? extends GrantedAuthority> roles;

		if (user.getAuthorities() != null || user.getAuthorities().size() > 0)
			roles = user.getAuthorities();
		else
			roles = roleRepository.getDefaultAuthoritiesForNewUser(user
					.getUsername());

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			UsersRecord newRecord = sql.newRecord(USERS);
			newRecord.setUserName(user.getUsername());
			newRecord.setEnabled(user.isEnabled());
			newRecord.setDateCreated(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setLastModified(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.store();

			Assert.notNull(newRecord.getUserId());

			credentialsRepository.create(newRecord.getUserId(),
					user.getPassword());

			for (GrantedAuthority role : roles) {
				roleRepository.create((Role) role);
				for (Organization organization : organizationRepository
						.getOrganizationFromAuthority(role.getAuthority())) {
					organizationMemberRepository.create(new OrganizationMember(
							organization.getOrganizationId(), newRecord
									.getUserId()));
				}
			}

		} catch (Exception e) {
			logger.info(e.toString());
			transaction.rollbackToSavepoint(savepoint);

		} finally {
			txManager.commit(transaction);

		}
	}

	@Override
	// @Transactional
	public void updateUser(UserDetails userDetails) {
		User user = (User) userDetails;
		Assert.notNull(user.getAuthorities());
		Assert.isTrue(!user.getAuthorities().isEmpty());

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {
			UsersRecord record = sql.fetchOne(USERS,
					USERS.USER_ID.eq(user.getUserId()));

			if (record == null)
				throw new UsernameNotFoundException("User with userName '"
						+ user.getUsername() + "' not found.");

			record.setEnabled(user.isEnabled());
			record.setLastModified(Timestamp.valueOf(LocalDateTime.now()));
			record.setFirstName(user.getFirstName());
			record.setMiddleName(user.getMiddleName());
			record.setLastName(user.getLastName());
			record.update();

			roleRepository.deleteByUserName(user.getUsername());
			organizationMemberRepository.deleteByUserId(user.getUserId());

			for (GrantedAuthority authority : user.getAuthorities()) {
				roleRepository.create((Role) authority);

				for (Organization organization : organizationRepository
						.getOrganizationFromAuthority(authority.getAuthority())) {
					organizationMemberRepository.create(new OrganizationMember(
							organization.getOrganizationId(), record
									.getUserId()));
				}
			}

		} catch (Exception e) {
			logger.info(e.toString());
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

			roleRepository.deleteByUserName(username);

			long userId = record.getUserId();
			organizationMemberRepository.deleteByUserId(userId);
			credentialsRepository.delete(userId);
			accountSettingRepository.delete(userId);

			sql.update(USERS).set(USERS.ENABLED, false)
					.where(USERS.USER_ID.eq(userId)).execute();

			// record.delete();

		} catch (Exception e) {
			logger.info(e.toString());
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

	private void updateCredential(String newPassword, Long userId) {
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
						accountNonLocked, roleRepository.getByUserName(record
								.getUserName()), record.getDateCreated()
								.toLocalDateTime(), record.getLastModified()
								.toLocalDateTime()))
				.collect(Collectors.toList());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	// @Transactional
	public void saveUsers(List<User> users) {
		final boolean enabled = true;

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		List collect = users
				.stream()
				.map(u -> sql.insertInto(USERS, USERS.USER_NAME, USERS.ENABLED,
						USERS.DATE_CREATED, USERS.LAST_MODIFIED).values(
						u.getUsername(), enabled,
						Timestamp.valueOf(LocalDateTime.now()),
						Timestamp.valueOf(LocalDateTime.now()))

				).collect(Collectors.toList());
		// for!
		for (User user : users) {
			for (GrantedAuthority authority : user.getAuthorities()) {
				collect.add(sql.insertInto(ROLES, ROLES.USER_NAME, ROLES.ROLE)
						.values(user.getUsername(), authority.getAuthority()));
				for (Organization organization : organizationRepository
						.getOrganizationFromAuthority(authority.getAuthority())) {
					sql.insertInto(ORGANIZATION_MEMBERS,
							ORGANIZATION_MEMBERS.ORGANIZATION_ID,
							ORGANIZATION_MEMBERS.USER_ID).values(
							organization.getOrganizationId(), user.getUserId());
				}

			}
		}

		try {
			sql.batch(collect).execute();

			// hmm, well, we won't be inserting THAT many users...
			for (User user : users) {
				Long userId = sql.select().from(USERS)
						.where(USERS.USER_NAME.eq(user.getUsername()))
						.fetchOne(USERS.USER_ID);
				credentialsRepository.create(new Long(userId),
						user.getPassword());
			}

		} catch (Exception e) {
			logger.info(e.toString());
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

	public List<User> getAdvisors(UserFilterRequest filter, User loggedUser,
			int limit) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Organization organization = sql.fetchOne(ORGANIZATION_MEMBERS,
				ORGANIZATION_MEMBERS.USER_ID.eq(loggedUser.getUserId())).into(
				Organization.class);

		String username = filter.getUserName();
		Long groupId = organization.getOrganizationId();

		/**
		 * select u.* from accounts.users u where username in ( select
		 * gm.username from accounts.group_members gm join accounts.groups g on
		 * gm.group_id = g.id where g.id = 0) and username in (select username
		 * from accounts.authorities where authority like 'ADVISOR');
		 * 
		 */
		Users u = USERS.as("u");
		Roles a = ROLES.as("a");
		OrganizationMembers gm = ORGANIZATION_MEMBERS.as("gm");
		Organizations g = ORGANIZATIONS.as("g");

		SelectConditionStep<Record1<Long>> usernamesFromSameGroup = sql
				.select(gm.USER_ID).from(gm).join(g)
				.on(gm.ORGANIZATION_ID.eq(g.ORGANIZATION_ID))
				.where(g.ORGANIZATION_ID.eq(groupId));

		SelectConditionStep<Record1<String>> advisorUsernames = sql
				.select(a.USER_NAME).from(a).where(a.ROLE.like("ADVISOR"));

		Result<Record> records = sql.select().from(u)
				.where(u.USER_ID.in(usernamesFromSameGroup))
				.and(u.USER_NAME.in(advisorUsernames))
				.and(u.USER_NAME.like("%" + username + "%")).limit(limit)
				.fetch();

		return advisorRecordsToUser(accountNonExpired, credentialsNonExpired,
				accountNonLocked, records);
	}

	public List<User> getAdvisors(User loggedUser, int limit) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Organization organization = sql.fetchOne(ORGANIZATION_MEMBERS,
				ORGANIZATION_MEMBERS.USER_ID.eq(loggedUser.getUserId())).into(
				Organization.class);

		Long groupId = organization.getOrganizationId();

		/**
		 * select u.* from accounts.users u where username in ( select
		 * gm.username from accounts.group_members gm join accounts.groups g on
		 * gm.group_id = g.id where g.id = 0) and username in (select username
		 * from accounts.authorities where authority like 'ADVISOR');
		 * 
		 */
		Users u = USERS.as("u");
		Roles a = ROLES.as("a");
		OrganizationMembers gm = ORGANIZATION_MEMBERS.as("gm");
		Organizations g = ORGANIZATIONS.as("g");

		SelectConditionStep<Record1<Long>> usernamesFromSameGroup = sql
				.select(gm.USER_ID).from(gm).join(g)
				.on(gm.ORGANIZATION_ID.eq(g.ORGANIZATION_ID))
				.where(g.ORGANIZATION_ID.eq(groupId));

		SelectConditionStep<Record1<String>> advisorUsernames = sql
				.select(a.USER_NAME).from(a).where(a.ROLE.like("ADVISOR"));

		Result<Record> records = sql.select().from(u)
				.where(u.USER_ID.in(usernamesFromSameGroup))
				.and(u.USER_NAME.in(advisorUsernames)).limit(limit).fetch();

		return advisorRecordsToUser(accountNonExpired, credentialsNonExpired,
				accountNonLocked, records);
	}

	private List<User> advisorRecordsToUser(final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked, Result<Record> records) {
		return records
				.stream()
				.map((Record ur) -> new User(ur.getValue(USERS.USER_ID), ur
						.getValue(USERS.FIRST_NAME), ur
						.getValue(USERS.MIDDLE_NAME), ur
						.getValue(USERS.LAST_NAME), ur
						.getValue(USERS.USER_NAME), credentialsRepository.get(
						ur.getValue(USERS.USER_ID)).getPassword(), ur
						.getValue(USERS.ENABLED), accountNonExpired,
						credentialsNonExpired, accountNonLocked, roleRepository
								.getByUserName(ur.getValue(USERS.USER_NAME)),
						ur.getValue(USERS.DATE_CREATED).toLocalDateTime(), ur
								.getValue(USERS.LAST_MODIFIED)
								.toLocalDateTime()))
				.collect(Collectors.toList());
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
}
