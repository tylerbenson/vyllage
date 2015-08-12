package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.OrganizationEnum;
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

		boolean userHasGuestOrganizationOnly = user
				.getAuthorities()
				.stream()
				.allMatch(
						uo -> ((UserOrganizationRole) uo).getOrganizationId()
								.equals(OrganizationEnum.GUESTS
										.getOrganizationId()));

		// return all advisors regardless of organization.
		if (userHasGuestOrganizationOnly)
			return sql().select(u.fields()).from(u).join(uor)
					.on(u.USER_ID.eq(uor.USER_ID))
					.where(uor.ROLE.eq(RolesEnum.ADVISOR.name()))
					.or(guestCondition(uor));

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

	@Override
	public List<User> backfill(User user, int limit) {
		Users u = USERS.as("u");
		UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");

		List<User> recordsToUser = new ArrayList<>();

		recordsToUser.addAll(recordsToUser(sql()
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
				.and(uor.ROLE.contains(RolesEnum.ADVISOR.name())).limit(limit)
				.fetch()));

		return recordsToUser;
	}

}
