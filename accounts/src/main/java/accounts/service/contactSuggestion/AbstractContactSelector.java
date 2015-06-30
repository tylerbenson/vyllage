package accounts.service.contactSuggestion;

import static accounts.domain.tables.Users.USERS;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import user.common.User;
import accounts.repository.UserOrganizationRoleRepository;

/**
 * Class that's used to suggest other users to parameterized one based on it's
 * roles and organization.
 * 
 * @author uh
 *
 */
@Component
public abstract class AbstractContactSelector {

	private String ROLE = "ADVISOR";

	@Autowired
	private final DSLContext sql = null;

	@Autowired
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	public List<User> select(User loggedInUser, Map<String, String> filters,
			int limit) {

		Optional<SelectConditionStep<Record>> suggestions = getSuggestions(loggedInUser);

		if (!suggestions.isPresent())
			return Collections.emptyList();

		if (filters != null && !filters.isEmpty())
			applyFilters(suggestions.get(), filters);
		return execute(suggestions.get(), limit);

	}

	protected List<User> execute(SelectConditionStep<Record> select, int limit) {
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		return recordsToUser(accountNonExpired, credentialsNonExpired,
				accountNonLocked, select.limit(limit).fetch());
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

	public String getROLE() {
		return ROLE;
	}

	public void setROLE(String ROLE) {
		this.ROLE = ROLE;
	}

	protected abstract Optional<SelectConditionStep<Record>> getSuggestions(
			User user);

	protected abstract void applyFilters(
			SelectConditionStep<Record> selectConditionStep,
			Map<String, String> filters);

}
