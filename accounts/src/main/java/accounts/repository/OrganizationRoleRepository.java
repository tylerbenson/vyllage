package accounts.repository;

import static accounts.domain.tables.OrganizationRoles.ORGANIZATION_ROLES;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.model.OrganizationRole;

@Repository
public class OrganizationRoleRepository {

	@Autowired
	private DSLContext sql;

	public OrganizationRole getGroupAuthorityFromGroup(long id) {

		return sql.fetchOne(ORGANIZATION_ROLES,
				ORGANIZATION_ROLES.ORGANIZATION_ID.eq(id)).into(OrganizationRole.class);
	}

}
