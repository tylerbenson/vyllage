package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;
import accounts.repository.UserOrganizationRoleRepository;

public class GuestContactSelector extends AbstractContactSelector {

	public GuestContactSelector(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository) {
		super(sql, userOrganizationRoleRepository);
	}

	@Override
	public Optional<SelectConditionStep<Record>> getSuggestions(
			User loggedInUser) {

		if (loggedInUser.isGuest()) {
			Users u = USERS.as("u");
			UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");

			SelectConditionStep<Record> select = sql()
					.select(u.fields())
					.from(u)
					.join(uor)
					.on(u.USER_ID.eq(uor.USER_ID))
					.where(uor.ORGANIZATION_ID.in(loggedInUser
							.getAuthorities()
							.stream()
							.map(a -> ((UserOrganizationRole) a)
									.getOrganizationId())
							.collect(Collectors.toList())))
					.and(uor.ROLE.contains(RolesEnum.ADMISSIONS_ADVISOR.name()));
			return Optional.of(select);
		}

		return Optional.empty();
	}

	@Override
	protected void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters) {
		// none for now
	}
}
