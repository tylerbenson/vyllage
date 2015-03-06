package accounts.repository;

import static accounts.domain.tables.OrganizationRoles.ORGANIZATION_ROLES;
import static accounts.domain.tables.Organizations.ORGANIZATIONS;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.OrganizationRoles;
import accounts.domain.tables.Organizations;
import accounts.model.Group;

@Repository
public class GroupRepository {
	@Autowired
	private DSLContext sql;

	public Group get(Long id) {
		return sql
				.fetchOne(ORGANIZATIONS, ORGANIZATIONS.ORGANIZATION_ID.eq(id))
				.into(Group.class);
	}

	public List<Group> getAll() {
		return sql.select().from(ORGANIZATIONS).fetch().into(Group.class);
	}

	public List<Group> getGroupFromAuthority(String authority) {
		Organizations g = ORGANIZATIONS.as("g");
		OrganizationRoles ga = ORGANIZATION_ROLES.as("ga");

		return sql.select(g.fields()).from(ga).join(g)
				.on(ga.ORGANIZATION_ID.eq(g.ORGANIZATION_ID))
				.where(ga.ROLE.eq(authority)).fetchInto(Group.class);
		/**
		 * select *.g from group_authorities ga join groups g on ga.group_id =
		 * g.id;
		 */
	}
}
