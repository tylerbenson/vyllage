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

	public List<UserRole> getByUserId(Long userId) {
		Result<UserRolesRecord> records = sql.fetch(USER_ROLES,
				USER_ROLES.USER_ID.eq(userId));

		return records.stream().map(UserRole::new).collect(Collectors.toList());
	}

	public void create(UserRole role) {
		UserRolesRecord auth = sql.newRecord(USER_ROLES);
		auth.setUserId(role.getUserId());
		auth.setRole(role.getAuthority());
		auth.insert();
	}

	public List<UserRole> getAll() {
		Result<UserRolesRecord> records = sql.fetch(USER_ROLES);

		return records.stream().map(UserRole::new).collect(Collectors.toList());
	}

	public void deleteByUserId(Long userId) {
		sql.delete(USER_ROLES).where(USER_ROLES.USER_ID.eq(userId)).execute();
	}

	/**
	 * Creates a new userRole without userid.
	 * 
	 * @return
	 */
	public List<UserRole> getDefaultAuthoritiesForNewUser() {
		UserRole auth = new UserRole(Roles.GUEST.name().toUpperCase(), null);
		return Arrays.asList(auth);
	}

}
