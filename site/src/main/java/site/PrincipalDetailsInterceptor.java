package site;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import user.common.UserOrganizationRole;

import com.newrelic.api.agent.NewRelic;

@Component
public class PrincipalDetailsInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		Principal userPrincipal = request.getUserPrincipal();
		if (userPrincipal != null) {
			NewRelic.addCustomParameter("email", userPrincipal.getName());
			if (userPrincipal instanceof UsernamePasswordAuthenticationToken) {
				UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) userPrincipal;
				for (GrantedAuthority grantedAuthority : token.getAuthorities()) {
					if (grantedAuthority instanceof UserOrganizationRole) {
						UserOrganizationRole uor = (UserOrganizationRole) grantedAuthority;
						NewRelic.addCustomParameter("userId", uor.getUserId());
						NewRelic.addCustomParameter(
								"userRole",
								uor.getOrganizationId() + "-"
										+ uor.getAuthority());
					}
				}
			}
		}
		return true;
	}
}
