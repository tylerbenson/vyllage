package site;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import site.service.DocumentService;
import user.common.User;
import user.common.UserOrganizationRole;

import com.newrelic.api.agent.NewRelic;

@Component
public class PrincipalDetailsInterceptor extends HandlerInterceptorAdapter {

	private final DocumentService documentService;

	@Inject
	public PrincipalDetailsInterceptor(final DocumentService documentService) {
		this.documentService = documentService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		final Principal userPrincipal = request.getUserPrincipal();

		if (userPrincipal != null) {

			NewRelic.addCustomParameter("email", userPrincipal.getName());

			if (request.getSession() != null)
				NewRelic.addCustomParameter("date-last-access", request
						.getSession(false).getCreationTime());

			if (userPrincipal instanceof UsernamePasswordAuthenticationToken) {
				UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) userPrincipal;

				if (token.getPrincipal() instanceof User) {
					User user = (User) token.getPrincipal();

					NewRelic.addCustomParameter("userId", user.getUserId());

					Long userDocumentLastModification = documentService
							.getUserDocumentLastModification(request,
									user.getUserId());

					System.out.println(userDocumentLastModification);

					NewRelic.addCustomParameter("date-document-last-modified",
							userDocumentLastModification);

					NewRelic.addCustomParameter("date-created-unix",
							this.getUserDateUnix(user.getDateCreated()));

					NewRelic.addCustomParameter("date-modified-unix",
							this.getUserDateUnix(user.getLastModified()));
				}

				int i = 0;

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
