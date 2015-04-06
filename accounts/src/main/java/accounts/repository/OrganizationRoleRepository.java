package accounts.repository;

import static accounts.domain.tables.OrganizationRoles.ORGANIZATION_ROLES;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.model.Role;

@Repository
public class OrganizationRoleRepository {

	@Autowired
	private DSLContext sql;

	public List<Role> getRolesForOrganization(long organizationId) {
		return sql.select().from(ORGANIZATION_ROLES)
				.where(ORGANIZATION_ROLES.ORGANIZATION_ID.eq(organizationId))
				.fetch().stream()
				.map(r -> new Role(r.getValue(ORGANIZATION_ROLES.ROLE)))
				.collect(Collectors.toList());
	}

}
