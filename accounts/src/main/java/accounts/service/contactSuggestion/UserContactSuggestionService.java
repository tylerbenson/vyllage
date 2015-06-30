package accounts.service.contactSuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import user.common.User;
import user.common.constants.RolesEnum;

@Service
public class UserContactSuggestionService {

	private Map<RolesEnum, AbstractContactSelector> roleToSelector = new HashMap<>();

	public UserContactSuggestionService() {
		roleToSelector.put(RolesEnum.GUEST, new GuestContactSelector());
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
