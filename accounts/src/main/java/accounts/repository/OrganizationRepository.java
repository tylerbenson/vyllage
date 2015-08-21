package accounts.repository;

import static accounts.domain.tables.Organizations.ORGANIZATIONS;
import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user.common.Organization;
import user.common.constants.OrganizationEnum;
import user.common.constants.RolesEnum;
import accounts.domain.tables.Organizations;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;

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

	public List<Organization> getAll(List<Long> organizationIds) {
		return sql.select().from(ORGANIZATIONS)
				.where(ORGANIZATIONS.ORGANIZATION_ID.in(organizationIds))
				.fetch().into(Organization.class);
	}

	public Organization getByExternalId(String externalOrganizationId) {
		return sql.fetchOne(ORGANIZATIONS,
				ORGANIZATIONS.EXTERNAL_ID.eq(externalOrganizationId)).into(
				Organization.class);
	}

	/**
	 * Returns the admin user assigned to the organization searching by external
	 * id.
	 * 
	 * @param externalOrganizationId
	 * @return admin id
	 */
	public Long getAuditUser(String externalOrganizationId) {
		UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");
		Organizations o = ORGANIZATIONS.as("o");
		Users u = USERS.as("u");

		Result<Record1<Long>> ids = sql.select(u.USER_ID).from(o).join(uor)
				.on(o.ORGANIZATION_ID.eq(uor.ORGANIZATION_ID)).join(u)
				.on(uor.USER_ID.eq(u.USER_ID))
				.where(o.EXTERNAL_ID.eq(externalOrganizationId))
				.and(uor.ROLE.contains(RolesEnum.ADMIN.name())).fetch();

		// this should never happen but just in case we return the Vyllage
		// admin.
		if (ids == null || ids.isEmpty())
			return sql
					.select(u.USER_ID)
					.from(o)
					.join(uor)
					.on(o.ORGANIZATION_ID.eq(uor.ORGANIZATION_ID))
					.join(u)
					.on(uor.USER_ID.eq(u.USER_ID))
					.where(o.ORGANIZATION_ID.eq(OrganizationEnum.VYLLAGE
							.getOrganizationId()))
					.and(uor.ROLE.contains(RolesEnum.ADMIN.name())).fetch()
					.get(0).value1();

		// the first one is fine
		return ids.get(0).value1();
	}
}
