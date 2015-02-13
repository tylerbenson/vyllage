package login.repository;

import static login.domain.tables.Users.USERS;

import java.util.List;
import java.util.logging.Logger;

import login.domain.tables.records.UsersRecord;
import login.model.Authority;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

@Repository
public class UserDetailRepository implements UserDetailsManager {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	private AuthenticationManager authenticationManager;

	@Autowired
	private DSLContext sql;

	@Autowired
	private AuthorityRepository authorityRepository;

	public UserDetailRepository() {
		// TODO Auto-generated constructor stub
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
	public void createUser(UserDetails user) {

		UsersRecord newRecord = sql.newRecord(USERS);
		newRecord.setUsername(user.getUsername());
		newRecord.setPassword(user.getPassword());
		newRecord.setEnabled(user.isEnabled());
		newRecord.store();

	}

	@Override
	public void updateUser(UserDetails user) {
		UsersRecord record = sql.fetchOne(USERS,
				USERS.USERNAME.eq(user.getUsername()));

		record.setPassword(user.getPassword());
		record.setEnabled(user.isEnabled());
	}

	@Override
	public void deleteUser(String username) {
		UsersRecord record = sql.fetchOne(USERS, USERS.USERNAME.eq(username));
		record.delete();
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

		sql.update(USERS).set(USERS.PASSWORD, newPassword)
				.where(USERS.USERNAME.eq(username)).execute();

		SecurityContextHolder.getContext().setAuthentication(
				createNewAuthentication(currentUser, newPassword));
	}

	protected Authentication createNewAuthentication(
			Authentication currentAuth, String newPassword) {
		UserDetails user = loadUserByUsername(currentAuth.getName());

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, null, user.getAuthorities());
		newAuthentication.setDetails(currentAuth.getDetails());

		return newAuthentication;
	}

	@Override
	public boolean userExists(String username) {
		return sql.fetchExists(sql.select().from(USERS)
				.where(USERS.USERNAME.eq(username)));
	}
}
