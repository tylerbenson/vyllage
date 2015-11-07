package accounts.repository;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import user.common.UserOrganizationRole;
import accounts.domain.tables.records.UserOrganizationRolesRecord;

@Repository
public class UserOrganizationRoleRepository {

	@Inject
	private DSLContext sql;

	public List<UserOrganizationRole> getByUserId(Long userId) {
		Result<UserOrganizationRolesRecord> records = sql.fetch(
				USER_ORGANIZATION_ROLES,
				USER_ORGANIZATION_ROLES.USER_ID.eq(userId));

		return records
				.stream()
				.map(r -> new UserOrganizationRole(r.getUserId(), r
						.getOrganizationId(), r.getRole(), r.getAuditUserId()))
				.collect(Collectors.toList());
	}

	public List<UserOrganizationRole> getByOrganizationId(Long organizationId) {
		Result<UserOrganizationRolesRecord> records = sql.fetch(
				USER_ORGANIZATION_ROLES,
				USER_ORGANIZATION_ROLES.ORGANIZATION_ID.eq(organizationId));

		return records
				.stream()
				.map(r -> new UserOrganizationRole(r.getUserId(), r
						.getOrganizationId(), r.getRole(), r.getAuditUserId()))
				.collect(Collectors.toList());
	}

	public void create(UserOrganizationRole role) {

		if (!this.exists(role.getUserId(), role.getOrganizationId(),
				role.getAuthority())) {

			UserOrganizationRolesRecord auth = sql
					.newRecord(USER_ORGANIZATION_ROLES);
			auth.setUserId(role.getUserId());
			auth.setRole(role.getAuthority());
			auth.setOrganizationId(role.getOrganizationId());
			auth.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
					.of("UTC"))));
			auth.setAuditUserId(role.getAuditUserId());
			auth.insert();
		}
	}

	public List<UserOrganizationRole> getAll() {
		Result<UserOrganizationRolesRecord> records = sql
				.fetch(USER_ORGANIZATION_ROLES);

		return records
				.stream()
				.map(r -> new UserOrganizationRole(r.getUserId(), r
						.getOrganizationId(), r.getRole(), r.getAuditUserId()))
				.collect(Collectors.toList());
	}

	/**
	 * Deletes ALL the user's organizations and roles.
	 *
	 * @param userId
	 */
	public void deleteByUserId(Long userId) {
		sql.delete(USER_ORGANIZATION_ROLES)
				.where(USER_ORGANIZATION_ROLES.USER_ID.eq(userId)).execute();
	}

	/**
	 * Deletes ALL the user's roles for an specific organization, including the
	 * organization relationship.
	 *
	 * @param userId
	 */
	public void deleteByUserIdAndOrganizationId(Long userId, Long organizationId) {
		sql.delete(USER_ORGANIZATION_ROLES)
				.where(USER_ORGANIZATION_ROLES.USER_ID.eq(userId).and(
						USER_ORGANIZATION_ROLES.ORGANIZATION_ID
								.eq(organizationId))).execute();
	}

	/**
	 * Checks if the given combination of userId, organizationId, and Role
	 * exists.
	 *
	 * @param userId
	 */
	public boolean exists(Long userId, Long organizationId, String role) {
		return sql.fetchExists(sql
				.select()
				.from(USER_ORGANIZATION_ROLES)
				.where(USER_ORGANIZATION_ROLES.USER_ID.eq(userId).and(
						USER_ORGANIZATION_ROLES.ORGANIZATION_ID.eq(
								organizationId).and(
								USER_ORGANIZATION_ROLES.ROLE.eq(role)))));
	}

}
