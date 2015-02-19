package login.repository;

import static login.domain.tables.Authorities.AUTHORITIES;
import static login.domain.tables.Users.USERS;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import login.domain.tables.records.UsersRecord;
import login.model.Authority;

import org.jooq.DSLContext;
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
			newRecord.setPassword(getEncodedPassword(user));
			newRecord.setEnabled(user.isEnabled());
			newRecord.store();

			for (GrantedAuthority grantedAuthority : authorities)
				authorityRepository.create((Authority) grantedAuthority);

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

			for (GrantedAuthority grantedAuthority : user.getAuthorities())
				authorityRepository.create((Authority) grantedAuthority);

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
		// sql.batchStore(userRecords).execute();
		// logger.info("Stored all records.");
		// this.getAll().forEach(System.out::println);
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
}
