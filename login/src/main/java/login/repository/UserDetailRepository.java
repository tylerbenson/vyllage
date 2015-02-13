package login.repository;

import static login.domain.tables.Users.USERS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import login.domain.tables.records.UsersRecord;
import login.model.Authority;
import login.model.UserDetail;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

@Repository
public class UserDetailRepository implements UserDetailsManager {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	@Autowired
	private DSLContext sql;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		UsersRecord record = sql.fetchOne(USERS, USERS.USERNAME.eq(username));

		logger.info("Looking for user " + username);

		if (record == null)
			throw new UsernameNotFoundException("Username " + username
					+ " not found.");

		Authority auth = new Authority();
		auth.setSetAuthority("ADMIN");

		List<Authority> authorities = new ArrayList<>();
		authorities.add(auth);

		UserDetail detail = new UserDetail();
		detail.setUserName(record.getUsername());
		detail.setPassword(record.getPassword());
		detail.setEnabled(record.getEnabled());
		detail.setAuthorities(authorities);
		logger.info(detail.toString());
		return detail;
	}

	@Override
	public void createUser(UserDetails user) {
		// TODO Auto-generated method stub

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

	@Override
	public boolean userExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}
}
