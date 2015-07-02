package accounts.service.contactSuggestion;

import static accounts.domain.tables.UserOrganizationRoles.USER_ORGANIZATION_ROLES;
import static accounts.domain.tables.Users.USERS;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.domain.tables.UserOrganizationRoles;
import accounts.domain.tables.Users;
import accounts.repository.UserOrganizationRoleRepository;

/**
 * Class that's used to suggest other users to the parameterized one based on
 * it's roles and organization.
 * 
 * @author uh
 *
 */
public abstract class AbstractContactSelector {

	private final DSLContext sql;

	private final UserOrganizationRoleRepository userOrganizationRoleRepository;

	public AbstractContactSelector(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository) {
		this.sql = sql;
		this.userOrganizationRoleRepository = userOrganizationRoleRepository;

	}

	public List<User> select(User user, Map<String, String> filters, int limit) {

		SelectConditionStep<Record> select = getSuggestions(user);

		return execute(user, select, filters, limit);

	}

	protected List<User> execute(User user, SelectConditionStep<Record> select,
			Map<String, String> filters, int limit) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		if (filters != null && !filters.isEmpty())
			applyFilters(select, filters);

		Result<Record> records = select.limit(limit).fetch();

		if (records == null || records.isEmpty()) {
			// maybe the org only has normal advisors, try to find those
			UserOrganizationRoles uor = USER_ORGANIZATION_ROLES.as("uor");
			Users u = USERS.as("u");

			select = sql()
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
					.and(uor.ROLE.contains(RolesEnum.ADVISOR.name()));

			applyFilters(select, filters);

			records = select.limit(limit).fetch();

		} else if (records == null || records.isEmpty())
			return Collections.emptyList(); // nothing...

		return recordsToUser(accountNonExpired, credentialsNonExpired,
				accountNonLocked, records);
	}

	private List<User> recordsToUser(final boolean accountNonExpired,
			final boolean credentialsNonExpired,
			final boolean accountNonLocked, Result<Record> records) {

		// we don't care about the password, we just need the user

		return records
				.stream()
				.map((Record ur) -> new User(ur.getValue(USERS.USER_ID), ur
						.getValue(USERS.FIRST_NAME), ur
						.getValue(USERS.MIDDLE_NAME), ur
						.getValue(USERS.LAST_NAME), ur
						.getValue(USERS.USER_NAME), "password", ur
						.getValue(USERS.ENABLED), accountNonExpired,
						credentialsNonExpired, accountNonLocked,
						userOrganizationRoleRepository.getByUserId(ur
								.getValue(USERS.USER_ID)), ur.getValue(
								USERS.DATE_CREATED).toLocalDateTime(), ur
								.getValue(USERS.LAST_MODIFIED)
								.toLocalDateTime()))
				.collect(Collectors.toList());
	}

	protected DSLContext sql() {
		return sql;
	}

	protected void applyFilters(SelectConditionStep<Record> select,
			Map<String, String> filters) {

		Users u = USERS.as("u");

		// TODO: find out a way to obtain the table field from u without using
		// the table fields names or a map for "tableField" -> "TABLE_FIELD"

		if (filters != null && !filters.isEmpty()) {

			if (filters.containsKey("firstName"))
				select.and(u.FIRST_NAME.like("%" + filters.get("firstName")
						+ "%"));

			if (filters.containsKey("lastName"))
				select.and(u.LAST_NAME.like("%" + filters.get("lastName") + "%"));

			if (filters.containsKey("email"))
				select.and(u.USER_NAME.contains(filters.get("email")));
		}
	}

	protected abstract SelectConditionStep<Record> getSuggestions(User user);

}
