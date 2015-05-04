package togglz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

public class CustomSpringSecurityUserProvider implements UserProvider {

	public static final String USER_ATTRIBUTE_ROLES = "roles";

	private final List<String> featureAdminAuthorities;

	public CustomSpringSecurityUserProvider(String... authorities) {
		featureAdminAuthorities = new ArrayList<>(Arrays.asList(authorities));
	}

	@Override
	public FeatureUser getCurrentUser() {

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		// null if no authentication data is available for the current thread
		if (authentication != null) {

			String name = null;

			// try to obtain the name of the user
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				name = userDetails.getUsername();
			} else {
				name = principal.toString();
			}

			Set<String> authorities = AuthorityUtils
					.authorityListToSet(authentication.getAuthorities());

			// check the authority
			boolean featureAdmin = false;
			if (featureAdminAuthorities != null
					&& !featureAdminAuthorities.isEmpty()) {
				featureAdmin = featureAdminAuthorities.stream().anyMatch(
						fa -> authorities.contains(fa));
			}

			SimpleFeatureUser user = new SimpleFeatureUser(name, featureAdmin);
			user.setAttribute(USER_ATTRIBUTE_ROLES, authorities);
			return user;

		}
		return null;
	}

}
