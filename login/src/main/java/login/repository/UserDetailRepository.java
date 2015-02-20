package login.repository;

import static login.domain.tables.Authorities.AUTHORITIES;
import static login.domain.tables.GroupMembers.GROUP_MEMBERS;
import static login.domain.tables.Groups.GROUPS;
import static login.domain.tables.Users.USERS;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

@Repository
public class UserDetailRepository implements UserDetailsManager {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

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
	private DataSourceTransactionManager txManager;

	public UserDetailRepository() {
	}

	@Override
	public User loadUserByUsername(String username)
			throws UsernameNotFoundException {

		// TODO: eventually we'll need these fields in the database.
		boolean accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true;

		UsersRecord record = sql.fetchOne(USERS, USERS.USERNAME.eq(username));

		logger.info("Looking for user " + username);

		if (record == null)
			throw new UsernameNotFoundException("User with username '"
					+ username + "' not found.");

		List<Authority> authorities = authorityRepository
				.getByUserName(username);

		User user = new User(record.getUsername(), record.getPassword(),
				record.getEnabled(), accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);

		logger.info(user.toString());
		return user;
	}

	@Override
	// @Transactional
	public void createUser(UserDetails user) {
		Assert.notNull(user.getAuthorities());
		Assert.isTrue(!user.getAuthorities().isEmpty());

		TransactionStatus transaction = txManager
				.getTransaction(new DefaultTransactionDefinition());

		Object savepoint = transaction.createSavepoint();

		try {

			UsersRecord newRecord = sql.newRecord(USERS);
			newRecord.setUsername(user.getUsername());
			newRecord.setPassword(getEncodedPassword(user));
			newRecord.setEnabled(user.isEnabled());
			newRecord.store();

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
			transaction.releaseSavepoint(savepoint);
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

			record.setPassword(getEncodedPassword(user));
			record.setEnabled(user.isEnabled());
			record.update();

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
			transaction.releaseSavepoint(savepoint);
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
			record.delete();

		} catch (Exception e) {
			logger.fine(e.toString());
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			transaction.releaseSavepoint(savepoint);
		}

	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext()
				.getAuthentication();
		String encodedNewPassword = new BCryptPasswordEncoder()
				.encode(newPassword);

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

		sql.update(USERS).set(USERS.PASSWORD, encodedNewPassword)
				.where(USERS.USERNAME.eq(username)).execute();

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
				.map((UsersRecord ur) -> new User(ur.getUsername(), ur
						.getPassword(), ur.getEnabled(), accountNonExpired,
						credentialsNonExpired, accountNonLocked,
						authorityRepository.getByUserName(ur.getUsername())))
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
				.map(u -> sql.insertInto(USERS, USERS.USERNAME, USERS.PASSWORD,
						USERS.ENABLED).values(u.getUsername(),
						getEncodedPassword(u), enabled)

				).collect(Collectors.toList());

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

		} catch (Exception e) {
			logger.fine(e.toString());
			transaction.rollbackToSavepoint(savepoint);
		} finally {
			transaction.releaseSavepoint(savepoint);
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

	private String getEncodedPassword(UserDetails user) {
		return new BCryptPasswordEncoder().encode(user.getPassword());
	}

	// public List<User> where(User user, int limit) {
	// List<Condition> conditions = new ArrayList<>();
	//
	// if (user.getUsername() != null)
	// conditions.add(USERS.USERNAME.like(user.getUsername()));
	//
	// conditions.add(USERS.ENABLED.eq(user.isEnabled()));
	//
	// for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
	// conditions.add(AUTHORITIES.AUTHORITY.like(grantedAuthority
	// .getAuthority()));
	// }
	//
	// return sql.select().from(USERS).join(AUTHORITIES)
	// .on(USERS.USERNAME.like(AUTHORITIES.USERNAME))
	// .where(conditions).limit(limit).fetch().into(User.class);
	// }

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
				.map((Record ur) -> new User(ur.getValue(USERS.USERNAME), ur
						.getValue(USERS.PASSWORD), ur.getValue(USERS.ENABLED),
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
				.map((Record ur) -> new User(ur.getValue(USERS.USERNAME), ur
						.getValue(USERS.PASSWORD), ur.getValue(USERS.ENABLED),
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, authorityRepository.getByUserName(ur
								.getValue(USERS.USERNAME))))
				.collect(Collectors.toList());
	}

}
