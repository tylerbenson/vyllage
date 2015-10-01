package site;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import user.common.User;
import user.common.UserOrganizationRole;

import com.newrelic.api.agent.NewRelic;

@Component
public class PrincipalDetailsInterceptor extends HandlerInterceptorAdapter {
	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd MMM yyyy");

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		final Principal userPrincipal = request.getUserPrincipal();

		if (userPrincipal != null) {

			NewRelic.addCustomParameter("email", userPrincipal.getName());

			if (userPrincipal instanceof User)
				NewRelic.addCustomParameter("date-created",
						this.getUserDateCreated(userPrincipal));

			if (userPrincipal instanceof User)
				NewRelic.addCustomParameter("userId",
						((User) userPrincipal).getUserId());

			if (userPrincipal instanceof UsernamePasswordAuthenticationToken) {

				int i = 0;
				UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) userPrincipal;

				for (GrantedAuthority grantedAuthority : token.getAuthorities()) {

					if (grantedAuthority instanceof UserOrganizationRole) {
						i++;
						UserOrganizationRole uor = (UserOrganizationRole) grantedAuthority;

						NewRelic.addCustomParameter(
								"userRole-" + i,
								uor.getOrganizationId() + "-"
										+ uor.getAuthority());
					}
				}
			}
		}
		return true;
	}

	protected String getUserDateCreated(Principal userPrincipal) {
		return ((User) userPrincipal).getDateCreated() != null ? ((User) userPrincipal)
				.getDateCreated().format(formatter)
				: "No creation date present";
	}
}
