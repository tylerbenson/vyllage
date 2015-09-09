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

import user.common.UserOrganizationRole;
import accounts.domain.tables.records.UserOrganizationRolesRecord;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;

@Repository
public class UserOrganizationRoleRepository {

	@Autowired
	private DSLContext sql;

	@Autowired
	private AccountSettingRepository accountSettingRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

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

			AccountSetting roleSetting = new AccountSetting();
			roleSetting.setName("role");
			roleSetting.setUserId(role.getUserId());
			roleSetting.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
			roleSetting.setValue(role.getAuthority());
			accountSettingRepository.set(roleSetting);

			AccountSetting organizationSetting = new AccountSetting();
			organizationSetting.setName("organization");
			organizationSetting.setUserId(role.getUserId());
			organizationSetting
					.setPrivacy(Privacy.PRIVATE.name().toLowerCase());
			organizationSetting.setValue(organizationRepository.get(
					role.getOrganizationId()).getOrganizationName());
			accountSettingRepository.set(organizationSetting);
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
