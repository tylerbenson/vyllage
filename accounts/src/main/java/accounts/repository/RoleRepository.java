package accounts.repository;

import static accounts.domain.tables.Roles.ROLES;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.model.Role;

@Repository
public class RoleRepository {

	@Autowired
	private DSLContext sql;

	public Role get(String role) {
		assert role != null && !role.isEmpty();
		return sql.fetchOne(ROLES, ROLES.ROLE.eq(role)).into(Role.class);
	}

	public List<Role> getAll() {
		return sql.fetch(ROLES).into(Role.class);
	}

}
