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
    public Authentication createAuthentication(HttpServletRequest request, ConsumerAuthentication authentication, OAuthAccessProviderToken authToken) {
        Collection<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        LMSRequest ltiRequest = (LMSRequest) request.getAttribute(LMSRequest.class.getName());
        if (ltiRequest == null) {
            throw new IllegalStateException("Cannot create authentication for LTI because the LTIRequest is null");
        }
        
        String username = ltiRequest.getLtiUserId();
        if (StringUtils.isBlank(username)) {
            username = authentication.getName();
        }

        if (ltiRequest.getUser() != null) {
            authorities.add(userGA);
        }
        if (ltiRequest.isRoleAdministrator()) {
            authorities.add(adminGA);
        }
        if (ltiRequest.isRoleInstructor()) {
            authorities.add(instructorGA);
        }
        if (ltiRequest.isRoleLearner()) {
            authorities.add(learnerGA);
        }

        Principal principal = new oauth.model.service.LMSOAuthAuthenticationHandler.NamedOAuthPrincipal(username, authorities,
                authentication.getConsumerCredentials().getConsumerKey(),
                authentication.getConsumerCredentials().getSignature(),
                authentication.getConsumerCredentials().getSignatureMethod(),
                authentication.getConsumerCredentials().getSignatureBaseString(),
                authentication.getConsumerCredentials().getToken()
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        return auth;
    }

}
