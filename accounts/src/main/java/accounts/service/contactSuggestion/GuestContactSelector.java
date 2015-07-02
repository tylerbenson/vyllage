package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

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

public class GuestContactSelector extends AbstractContactSelector {

	public GuestContactSelector(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository) {
		super(sql, userOrganizationRoleRepository);
	}

	@Override
	protected SelectConditionStep<Record> getSuggestions(User user) {

		UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");

		Users u = USERS.as("u");

		return sql()
				.select(u.fields())
				.from(u)
				.join(uor)
				.on(u.USER_ID.eq(uor.USER_ID))
				.where(uor.ORGANIZATION_ID.in(user
						.getAuthorities()
						.stream()
						.map(a -> ((UserOrganizationRole) a)
								.getOrganizationId())
						.collect(Collectors.toList())))
				.or(uor.ROLE.eq(RolesEnum.ADVISOR.name()))
				.and(guestCondition(uor));
	}

	private Condition guestCondition(UserOrganizationRoles uor) {
		return uor.ROLE.contains(RolesEnum.ADMISSIONS_ADVISOR.name());
	}

}
