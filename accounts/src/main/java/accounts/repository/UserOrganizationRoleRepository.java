package accounts.repository;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import accounts.domain.tables.records.UserOrganizationRolesRecord;
import accounts.model.UserOrganizationRole;

@Repository
public class UserOrganizationRoleRepository {

	@Autowired
	private DSLContext sql;

	public List<UserOrganizationRole> getByUserId(Long userId) {
		Result<UserOrganizationRolesRecord> records = sql.fetch(
				USER_ORGANIZATION_ROLES,
				USER_ORGANIZATION_ROLES.USER_ID.eq(userId));

		return records.stream().map(UserOrganizationRole::new)
				.collect(Collectors.toList());
	}

	public void create(UserOrganizationRole role) {
		UserOrganizationRolesRecord auth = sql
				.newRecord(USER_ORGANIZATION_ROLES);
		auth.setUserId(role.getUserId());
		auth.setRole(role.getAuthority());
		auth.setOrganizationId(role.getOrganizationId());
		auth.setDateCreated(Timestamp.valueOf(LocalDateTime.now(ZoneId
				.of("UTC"))));
		auth.insert();
	}

	public List<UserOrganizationRole> getAll() {
		Result<UserOrganizationRolesRecord> records = sql
				.fetch(USER_ORGANIZATION_ROLES);

		return records.stream().map(UserOrganizationRole::new)
				.collect(Collectors.toList());
	}

	/**
	 * Deletes ALL user's organizations and roles.
	 * 
	 * @param userId
	 */
	public void deleteByUserId(Long userId) {
		sql.delete(USER_ORGANIZATION_ROLES)
				.where(USER_ORGANIZATION_ROLES.USER_ID.eq(userId)).execute();
	}

	/**
	 * Deletes ALL user's organizations and roles.
	 * 
	 * @param userId
	 */
	public void deleteByUserIdAndOrganizationId(Long userId, Long organizationId) {
		sql.delete(USER_ORGANIZATION_ROLES)
				.where(USER_ORGANIZATION_ROLES.USER_ID.eq(userId).and(
						USER_ORGANIZATION_ROLES.ORGANIZATION_ID
								.eq(organizationId))).execute();
	}
}
