package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Component;

import user.common.User;
import user.common.UserOrganizationRole;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;

@Component
public class GuestContactSelector extends AbstractContactSelector {

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
					.and(uor.ROLE.contains(getROLE()));
			return Optional.of(select);

		} else
			return Optional.empty();
	}

	@Override
	protected void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters) {
		// none for now
	}
}
