package accounts.service.contactSuggestion;

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

	public List<User> select(User loggedInUser, Map<String, String> filters,
			int limit) {

		SelectConditionStep<Record> suggestions = getSuggestions(loggedInUser);

		if (filters != null && !filters.isEmpty())
			applyFilters(suggestions, filters);
		return execute(suggestions, limit);

	}

	protected List<User> execute(SelectConditionStep<Record> select, int limit) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Result<Record> records = select.limit(limit).fetch();

		if (records == null || records.isEmpty())
			return Collections.emptyList();

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

	public DSLContext sql() {
		return sql;
	}

	protected abstract SelectConditionStep<Record> getSuggestions(User user);

	protected abstract void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters);

}
