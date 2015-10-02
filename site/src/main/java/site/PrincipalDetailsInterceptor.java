package site;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		final Principal userPrincipal = request.getUserPrincipal();

		if (userPrincipal != null) {

			NewRelic.addCustomParameter("email", userPrincipal.getName());

			if (request.getSession() != null)
				NewRelic.addCustomParameter("date-last-access", request
						.getSession(false).getCreationTime());

			if (userPrincipal instanceof User) {
				NewRelic.addCustomParameter("userId",
						((User) userPrincipal).getUserId());

				NewRelic.addCustomParameter("date-created-unix", this
						.getUserDateUnix(((User) userPrincipal)
								.getDateCreated()));

				NewRelic.addCustomParameter("date-modified-unix", this
						.getUserDateUnix(((User) userPrincipal)
								.getLastModified()));
			}

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

	protected long getUserDateUnix(LocalDateTime date) {
		return date != null ? date.toInstant(ZoneOffset.UTC).toEpochMilli() : 0;
	}
}
