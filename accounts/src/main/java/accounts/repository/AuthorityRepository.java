package accounts.repository;

import static accounts.domain.tables.Roles.ROLES;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.RolesRecord;
import accounts.model.Authority;

@Repository
public class AuthorityRepository {

	@Autowired
	private DSLContext sql;

	public List<Authority> getByUserName(String userName) {
		Result<RolesRecord> records = sql.fetch(ROLES,
				ROLES.USER_NAME.eq(userName));

		return records.stream().map(Authority::new)
				.collect(Collectors.toList());
	}

	public void create(Authority authority) {
		RolesRecord auth = sql.newRecord(ROLES);
		auth.setUserName(authority.getUserName());
		auth.setRole(authority.getAuthority());
		auth.insert();
	}

	public List<Authority> getAll() {
		Result<RolesRecord> records = sql.fetch(ROLES);

		return records.stream().map(Authority::new)
				.collect(Collectors.toList());
	}

	public void deleteByUserName(String userName) {
		sql.delete(ROLES).where(ROLES.USER_NAME.eq(userName)).execute();
	}

	public List<Authority> getDefaultAuthoritiesForNewUser(String userName) {
		Authority auth = new Authority("USER", userName);
		return Arrays.asList(auth);
	}

}
