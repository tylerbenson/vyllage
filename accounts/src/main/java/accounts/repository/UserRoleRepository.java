package accounts.repository;

import static accounts.domain.tables.UserRoles.USER_ROLES;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.constants.Roles;
import accounts.domain.tables.records.UserRolesRecord;
import accounts.model.UserRole;

@Repository
public class UserRoleRepository {

	@Autowired
	private DSLContext sql;

	public List<UserRole> getByUserName(String userName) {
		Result<UserRolesRecord> records = sql.fetch(USER_ROLES,
				USER_ROLES.USER_NAME.eq(userName));

		return records.stream().map(UserRole::new).collect(Collectors.toList());
	}

	public void create(UserRole role) {
		UserRolesRecord auth = sql.newRecord(USER_ROLES);
		auth.setUserName(role.getUserName());
		auth.setRole(role.getAuthority());
		auth.insert();
	}

	public List<UserRole> getAll() {
		Result<UserRolesRecord> records = sql.fetch(USER_ROLES);

		return records.stream().map(UserRole::new).collect(Collectors.toList());
	}

	public void deleteByUserName(String userName) {
		sql.delete(USER_ROLES).where(USER_ROLES.USER_NAME.eq(userName))
				.execute();
	}

	public List<UserRole> getDefaultAuthoritiesForNewUser(String userName) {
		UserRole auth = new UserRole(Roles.GUEST.name().toUpperCase(), userName);
		return Arrays.asList(auth);
	}

}
