package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;
import accounts.repository.UserOrganizationRoleRepository;

public class AlumniContactSelector extends AbstractContactSelector {

	public AlumniContactSelector(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository) {
		super(sql, userOrganizationRoleRepository);
	}

	@Override
	protected SelectConditionStep<Record> getSuggestions(User user) {
		UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");
		Users u = USERS.as("u");

		List<Long> organizationIds = user.getAuthorities().stream()
				.map(a -> ((UserOrganizationRole) a).getOrganizationId())
				.collect(Collectors.toList());

		return sql()
				.select(u.fields())
				.from(u)
				.join(uor)
				.on(u.USER_ID.eq(uor.USER_ID))
				.where(uor.ORGANIZATION_ID.in(organizationIds))
				.and(alumniSearchCondition(uor));
	}

	private Condition alumniSearchCondition(UserOrganizationRoles uor) {
		return uor.ROLE.contains(RolesEnum.CAREER_ADVISOR.name()).or(
				uor.ROLE.contains(RolesEnum.TRANSFER_ADVISOR.name()));
	}

	@Override
	protected void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters) {
		// nothing right now

	}

}
