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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class UserDetailRepository implements UserDetailsService {

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
		auth.setSetAuthority("USER");

		List<Authority> authorities = new ArrayList<>();
		authorities.add(auth);

		UserDetail detail = new UserDetail();
		detail.setUserName(record.getUsername());
		detail.setPassword(record.getPassword());
		detail.setEnabled(record.getEnabled());
		detail.setAuthorities(authorities);
		return detail;
	}
}
