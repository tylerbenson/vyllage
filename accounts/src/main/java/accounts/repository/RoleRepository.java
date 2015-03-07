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
import accounts.model.Role;

@Repository
public class RoleRepository {

	@Autowired
	private DSLContext sql;

	public List<Role> getByUserName(String userName) {
		Result<RolesRecord> records = sql.fetch(ROLES,
				ROLES.USER_NAME.eq(userName));

		return records.stream().map(Role::new)
				.collect(Collectors.toList());
	}

	public void create(Role role) {
		RolesRecord auth = sql.newRecord(ROLES);
		auth.setUserName(role.getUserName());
		auth.setRole(role.getAuthority());
		auth.insert();
	}

	public List<Role> getAll() {
		Result<RolesRecord> records = sql.fetch(ROLES);

		return records.stream().map(Role::new)
				.collect(Collectors.toList());
	}

	public void deleteByUserName(String userName) {
		sql.delete(ROLES).where(ROLES.USER_NAME.eq(userName)).execute();
	}

	public List<Role> getDefaultAuthoritiesForNewUser(String userName) {
		Role auth = new Role("USER", userName);
		return Arrays.asList(auth);
	}

}
