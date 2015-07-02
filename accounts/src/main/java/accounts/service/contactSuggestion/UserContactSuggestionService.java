package accounts.service.contactSuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
	}

	public List<User> getSuggestions(final User loggedInUser,
			Map<String, String> filters, int limit) {

		List<User> users = new ArrayList<>();

		for (GrantedAuthority grantedAuthority : loggedInUser.getAuthorities()) {
			RolesEnum rolesEnum = RolesEnum.valueOf(grantedAuthority
					.getAuthority());
			if (roleToSelector.containsKey(rolesEnum))
				users.addAll(roleToSelector.get(rolesEnum).select(loggedInUser,
						filters, limit));

		}

		return users;
	}

}
