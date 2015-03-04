package login.repository;

import static login.domain.tables.Authorities.AUTHORITIES;
import static login.domain.tables.GroupMembers.GROUP_MEMBERS;
import static login.domain.tables.Groups.GROUPS;
import static login.domain.tables.Users.USERS;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import login.domain.tables.Authorities;
import login.domain.tables.GroupMembers;
import login.domain.tables.Groups;
import login.domain.tables.Users;
import login.domain.tables.records.UsersRecord;
import login.model.Authority;
import login.model.Group;
import login.model.GroupMember;
import login.model.User;
import login.model.UserCredential;
import login.model.UserFilterRequest;

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

@Repository
public class UserDetailRepository implements UserDetailsManager {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	// @Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DSLContext sql;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	@Autowired
	private UserCredentialsRepository credentialsRepository;

	@Autowired
	private DataSourceTransactionManager txManager;

	public UserDetailRepository() {
	}

	public User get(Long userId) throws UserNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USERID.eq(userId));

		if (record == null)
			throw new UserNotFoundException("User with id '" + userId
					+ "' not found.");

		User user = getUserData(record);

		return user;

	}

	@Override
	public User loadUserByUsername(String username)
			throws UsernameNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USERNAME.eq(username));

		if (record == null)
			throw new UsernameNotFoundException("User with username '"
					+ username + "' not found.");

		User user = getUserData(record);

		return user;
	}

	protected User getUserData(UsersRecord record) {

		// TODO: eventually we'll need these fields in the database.
		boolean accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true;

		List<Authority> authorities = authorityRepository.getByUserName(record
				.getUsername());

		UserCredential credential = credentialsRepository.get(record
				.getUserid());

		User user = new User(record.getUserid(), record.getFirstname(),
				record.getMiddlename(), record.getLastname(),
				record.getUsername(), credential.getPassword(),
				record.getEnabled(), accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities, record.getDatecreated()
						.toLocalDateTime(), record.getLastmodified()
						.toLocalDateTime());
		return user;
	}

	@Override
	// @Transactional
	public void createUser(UserDetails user) {

		Collection<? extends GrantedAuthority> authorities;

		if (user.getAuthorities() != null || user.getAuthorities().size() > 0)
			authorities = user.getAuthorities();
		else
			authorities = authorityRepository
					.getDefaultAuthoritiesForNewUser(user.getUsername());

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			UsersRecord newRecord = sql.newRecord(USERS);
			newRecord.setUsername(user.getUsername());
			newRecord.setEnabled(user.isEnabled());
			newRecord.setDatecreated(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));
			newRecord.store();

			Assert.notNull(newRecord.getUserid());

			credentialsRepository.save(newRecord.getUserid(),
					user.getPassword());

			for (GrantedAuthority authority : authorities) {
				authorityRepository.create((Authority) authority);
				for (Group group : groupRepository
						.getGroupFromAuthority(authority.getAuthority())) {
					groupMemberRepository.create(new GroupMember(group.getId(),
							user.getUsername()));
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
	public void updateUser(UserDetails user) {
		Assert.notNull(user.getAuthorities());
		Assert.isTrue(!user.getAuthorities().isEmpty());

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {
			UsersRecord record = sql.fetchOne(USERS,
					USERS.USERNAME.eq(user.getUsername()));

			record.setEnabled(user.isEnabled());
			record.setLastmodified(Timestamp.valueOf(LocalDateTime.now()));
			record.update();

			credentialsRepository.save(record.getUserid(), user.getPassword());

			authorityRepository.deleteByUserName(user.getUsername());
			groupMemberRepository.deleteByUserName(user.getUsername());

			for (GrantedAuthority authority : user.getAuthorities()) {
				authorityRepository.create((Authority) authority);

				for (Group group : groupRepository
						.getGroupFromAuthority(authority.getAuthority())) {
					groupMemberRepository.create(new GroupMember(group.getId(),
							user.getUsername()));
				}
			}

		} catch (Exception e) {
			logger.fine(e.toString());
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			txManager.commit(transaction);
		}
	}

	@Override
	// @Transactional
	public void deleteUser(String username) {
		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {
			authorityRepository.deleteByUserName(username);
			groupMemberRepository.deleteByUserName(username);

			UsersRecord record = sql.fetchOne(USERS,
					USERS.USERNAME.eq(username));

			long userId = record.getUserid();
			credentialsRepository.delete(userId);

			record.delete();

		} catch (Exception e) {
			logger.fine(e.toString());
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
		credentialsRepository.delete(userId);
		credentialsRepository.save(userId, newPassword);

		SecurityContextHolder.getContext().setAuthentication(
				createNewAuthentication(currentUser, newPassword));
	}

	@Override
	public boolean userExists(String username) {
		return sql.fetchExists(sql.select().from(USERS)
				.where(USERS.USERNAME.eq(username)));
	}

	public List<User> getAll() {

		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		return sql
				.fetch(USERS)
				.stream()
				.map((UsersRecord record) -> new User(record.getUserid(),
						record.getFirstname(), record.getMiddlename(), record
								.getLastname(), record.getUsername(),
						credentialsRepository.get(record.getUserid())
								.getPassword(), record.getEnabled(),
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, authorityRepository
								.getByUserName(record.getUsername()), record
								.getDatecreated().toLocalDateTime(), record
								.getLastmodified().toLocalDateTime()))
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
				.map(u -> sql.insertInto(USERS, USERS.USERNAME, USERS.ENABLED,
						USERS.DATECREATED, USERS.LASTMODIFIED).values(
						u.getUsername(), enabled,
						Timestamp.valueOf(LocalDateTime.now()),
						Timestamp.valueOf(LocalDateTime.now()))

				).collect(Collectors.toList());
		// for!
		for (User user : users) {
			for (GrantedAuthority authority : user.getAuthorities()) {
				collect.add(sql.insertInto(AUTHORITIES, AUTHORITIES.USERNAME,
						AUTHORITIES.AUTHORITY).values(user.getUsername(),
						authority.getAuthority()));
				for (Group group : groupRepository
						.getGroupFromAuthority(authority.getAuthority())) {
					sql.insertInto(GROUP_MEMBERS, GROUP_MEMBERS.GROUP_ID,
							GROUP_MEMBERS.USERNAME).values(group.getId(),
							user.getUsername());
				}

			}
		}

		try {
			sql.batch(collect).execute();

			// hmm, well, we won't be inserting THAT many users...
			for (User user : users) {
				Long userId = sql.select().from(USERS)
						.where(USERS.USERNAME.eq(user.getUsername()))
						.fetchOne(USERS.USERID);
				credentialsRepository
						.save(new Long(userId), user.getPassword());
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

		Group group = sql.fetchOne(GROUP_MEMBERS,
				GROUP_MEMBERS.USERNAME.eq(loggedUser.getUsername())).into(
				Group.class);

		String username = filter.getUserName();
		Long groupId = group.getId();

		/**
		 * select u.* from login.users u where username in ( select gm.username
		 * from login.group_members gm join login.groups g on gm.group_id = g.id
		 * where g.id = 0) and username in (select username from
		 * login.authorities where authority like 'ADVISOR');
		 * 
		 */
		Users u = USERS.as("u");
		Authorities a = AUTHORITIES.as("a");
		GroupMembers gm = GROUP_MEMBERS.as("gm");
		Groups g = GROUPS.as("g");

		SelectConditionStep<Record1<String>> usernamesFromSameGroup = sql
				.select(gm.USERNAME).from(gm).join(g).on(gm.GROUP_ID.eq(g.ID))
				.where(g.ID.eq(groupId));

		SelectConditionStep<Record1<String>> advisorUsernames = sql
				.select(a.USERNAME).from(a).where(a.AUTHORITY.like("ADVISOR"));

		Result<Record> records = sql.select().from(u)
				.where(u.USERNAME.in(usernamesFromSameGroup))
				.and(u.USERNAME.in(advisorUsernames))
				.and(u.USERNAME.like("%" + username + "%")).limit(limit)
				.fetch();

		return records
				.stream()
				.map((Record ur) -> new User(ur.getValue(USERS.USERNAME),
						credentialsRepository.get(ur.getValue(USERS.USERID))
								.getPassword(), ur.getValue(USERS.ENABLED),
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, authorityRepository.getByUserName(ur
								.getValue(USERS.USERNAME))))
				.collect(Collectors.toList());
	}

	public List<User> getAdvisors(User loggedUser, int limit) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Group group = sql.fetchOne(GROUP_MEMBERS,
				GROUP_MEMBERS.USERNAME.eq(loggedUser.getUsername())).into(
				Group.class);

		Long groupId = group.getId();

		/**
		 * select u.* from login.users u where username in ( select gm.username
		 * from login.group_members gm join login.groups g on gm.group_id = g.id
		 * where g.id = 0) and username in (select username from
		 * login.authorities where authority like 'ADVISOR');
		 * 
		 */
		Users u = USERS.as("u");
		Authorities a = AUTHORITIES.as("a");
		GroupMembers gm = GROUP_MEMBERS.as("gm");
		Groups g = GROUPS.as("g");

		SelectConditionStep<Record1<String>> usernamesFromSameGroup = sql
				.select(gm.USERNAME).from(gm).join(g).on(gm.GROUP_ID.eq(g.ID))
				.where(g.ID.eq(groupId));

		SelectConditionStep<Record1<String>> advisorUsernames = sql
				.select(a.USERNAME).from(a).where(a.AUTHORITY.like("ADVISOR"));

		Result<Record> records = sql.select().from(u)
				.where(u.USERNAME.in(usernamesFromSameGroup))
				.and(u.USERNAME.in(advisorUsernames)).limit(limit).fetch();

		return records
				.stream()
				.map((Record ur) -> new User(ur.getValue(USERS.USERNAME),
						credentialsRepository.get(ur.getValue(USERS.USERID))
								.getPassword(), ur.getValue(USERS.ENABLED),
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, authorityRepository.getByUserName(ur
								.getValue(USERS.USERNAME))))
				.collect(Collectors.toList());
	}

}
