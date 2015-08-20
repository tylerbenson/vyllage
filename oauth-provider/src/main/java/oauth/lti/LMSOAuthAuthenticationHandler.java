package oauth.lti;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.provider.ConsumerAuthentication;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.stereotype.Component;

import oauth.utilities.LMSConstants;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;

@Component
public class LMSOAuthAuthenticationHandler implements OAuthAuthenticationHandler {

	final static Logger log = LoggerFactory.getLogger(LMSOAuthAuthenticationHandler.class);

	public static SimpleGrantedAuthority userGA = new SimpleGrantedAuthority("ROLE_USER");
	public static SimpleGrantedAuthority learnerGA = new SimpleGrantedAuthority("ROLE_LEARNER");
	public static SimpleGrantedAuthority instructorGA = new SimpleGrantedAuthority("ROLE_INSTRUCTOR");
	public static SimpleGrantedAuthority adminGA = new SimpleGrantedAuthority("ROLE_ADMIN");

	@Override
	public Authentication createAuthentication(HttpServletRequest request, ConsumerAuthentication authentication,
			OAuthAccessProviderToken authToken) {

		Collection<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
		LMSRequest lmsRequest = (LMSRequest) request.getAttribute(LMSRequest.class.getName());
		if (lmsRequest == null) {
			throw new IllegalStateException(LMSConstants.LTI_INVALID_LTIREQ);
		}
		String username = lmsRequest.getLmsUser().getUserId();
		if (StringUtils.isBlank(username)) {
			username = authentication.getName();
		}
		if (lmsRequest.isRoleAdministrator()) {
			authorities.add(adminGA);
		}
		if (lmsRequest.isRoleInstructor()) {
			authorities.add(instructorGA);
		}
		if (lmsRequest.isRoleLearner()) {
			authorities.add(learnerGA);
		}

		Principal principal = new oauth.model.service.LMSAuthenticationHandler.NamedOAuthPrincipal(username,
				authorities, authentication.getConsumerCredentials().getConsumerKey(),
				authentication.getConsumerCredentials().getSignature(),
				authentication.getConsumerCredentials().getSignatureMethod(),
				authentication.getConsumerCredentials().getSignatureBaseString(),
				authentication.getConsumerCredentials().getToken());
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
		return auth;
	}

}
