package accounts.service.contactSuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.NonNull;

import org.jooq.DSLContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import user.common.User;
import user.common.constants.RolesEnum;
import accounts.repository.UserOrganizationRoleRepository;
import accounts.service.AccountSettingsService;

@Service
public class UserContactSuggestionService {

	private Map<RolesEnum, AbstractContactSelector> roleToSelector = new HashMap<>();

	@Inject
	public UserContactSuggestionService(DSLContext sql,
			UserOrganizationRoleRepository userOrganizationRoleRepository,
			AccountSettingsService accountSettingsService) {

		roleToSelector.put(RolesEnum.GUEST, new GuestContactSelector(sql,
				userOrganizationRoleRepository));

		roleToSelector
				.put(RolesEnum.STUDENT, new CurrentStudentContactSelector(sql,
						userOrganizationRoleRepository, accountSettingsService));

		roleToSelector.put(RolesEnum.ALUMNI, new AlumniContactSelector(sql,
				userOrganizationRoleRepository));

		roleToSelector.put(RolesEnum.ADMIN, new AdminContactSelector(sql,
				userOrganizationRoleRepository));

		// same
		roleToSelector.put(RolesEnum.LMS_ADMIN,
				roleToSelector.get(RolesEnum.ADMIN));

		roleToSelector.put(RolesEnum.STAFF, new StaffContactSelector(sql,
				userOrganizationRoleRepository));

		roleToSelector.put(RolesEnum.ADVISOR, new AdvisorContactSelector(sql,
				userOrganizationRoleRepository));

		// same for all
		roleToSelector.put(RolesEnum.ACADEMIC_ADVISOR,
				roleToSelector.get(RolesEnum.ADVISOR));

		roleToSelector.put(RolesEnum.ADMISSIONS_ADVISOR,
				roleToSelector.get(RolesEnum.ADVISOR));

		roleToSelector.put(RolesEnum.CAREER_ADVISOR,
				roleToSelector.get(RolesEnum.ADVISOR));

		roleToSelector.put(RolesEnum.INSTRUCTOR,
				roleToSelector.get(RolesEnum.ADVISOR));

		roleToSelector.put(RolesEnum.TEACHING_ASSISTANT,
				roleToSelector.get(RolesEnum.ADVISOR));

		roleToSelector.put(RolesEnum.TRANSFER_ADVISOR,
				roleToSelector.get(RolesEnum.ADVISOR));
	}

	public List<User> getSuggestions(@NonNull final User user,
			Map<String, String> filters, int limit) {

		List<User> users = new ArrayList<>();

		for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
			int newLimit = limit - users.size();

			RolesEnum rolesEnum = RolesEnum.valueOf(grantedAuthority
					.getAuthority());
			if (roleToSelector.containsKey(rolesEnum)
					&& users.size() < newLimit)
				users.addAll(roleToSelector.get(rolesEnum).select(user,
						filters, newLimit));
		}

		users = backfill(user, users, limit);

		return users;
	}

	/**
	 * If the list of user is less than the limit then we get more suggestions.
	 *
	 * @param user
	 * @param users
	 * @param limit
	 */
	protected List<User> backfill(@NonNull final User user,
			@NonNull final List<User> users, final int limit) {

		if (users.isEmpty() || users.size() <= limit) {
			int newLimit = limit - users.size();

			for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
				RolesEnum rolesEnum = RolesEnum.valueOf(grantedAuthority
						.getAuthority());
				if (roleToSelector.containsKey(rolesEnum)
						&& users.size() < limit) {

					List<User> backfill = roleToSelector.get(rolesEnum)
							.backfill(user, newLimit);

					if (backfill != null && !backfill.isEmpty())
						users.addAll(backfill);
				}
			}
		}

		return users;
	}
}
