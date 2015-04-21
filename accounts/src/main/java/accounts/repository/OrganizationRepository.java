package accounts.repository;

import static accounts.domain.tables.Organizations.ORGANIZATIONS;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.model.Organization;

@Repository
public class OrganizationRepository {
	@Autowired
	private DSLContext sql;

	public Organization get(Long id) {
		return sql
				.fetchOne(ORGANIZATIONS, ORGANIZATIONS.ORGANIZATION_ID.eq(id))
				.into(Organization.class);
	}

	public Organization getByName(String name) {
		return sql.fetchOne(ORGANIZATIONS,
				ORGANIZATIONS.ORGANIZATION_NAME.eq(name)).into(
				Organization.class);
	}

	public List<Organization> getAll() {
		return sql.select().from(ORGANIZATIONS).fetch()
				.into(Organization.class);
	}

	// public List<Organization> getOrganizationFromAuthority(String authority)
	// {
	// Organizations g = ORGANIZATIONS.as("g");
	// OrganizationRoles ga = ORGANIZATION_ROLES.as("ga");
	//
	// return sql.select(g.fields()).from(ga).join(g)
	// .on(ga.ORGANIZATION_ID.eq(g.ORGANIZATION_ID))
	// .where(ga.ROLE.eq(authority)).fetchInto(Organization.class);
	// /**
	// * select *.g from group_authorities ga join groups g on ga.group_id =
	// * g.id;
	// */
	// }

}
