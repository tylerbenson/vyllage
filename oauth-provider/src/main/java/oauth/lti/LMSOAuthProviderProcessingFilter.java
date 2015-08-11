package oauth.lti;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;

import oauth.model.service.LMSOAuthNonceServices;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LMSOAuthProviderProcessingFilter extends ProtectedResourceProcessingFilter {


    public LMSOAuthProviderProcessingFilter(LMSConsumerDetailsService oAuthConsumerDetailsService, LMSOAuthNonceServices oAuthNonceServices, OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint, LMSOAuthAuthenticationHandler oAuthAuthenticationHandler, OAuthProviderTokenServices oAuthProviderTokenServices) {
        super();
        setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
        setAuthHandler(oAuthAuthenticationHandler);
        setConsumerDetailsService(oAuthConsumerDetailsService);
        setNonceServices(oAuthNonceServices);
        setTokenServices(oAuthProviderTokenServices);
        
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        
    	if (!(servletRequest instanceof HttpServletRequest)) {
            throw new IllegalStateException("LTI request MUST be an HttpServletRequest (cannot only be a ServletRequest)");
        }
    	HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        LMSRequest ltiRequest = new LMSRequest(httpServletRequest); 
        httpServletRequest.setAttribute("LTI", true); 
        httpServletRequest.setAttribute("lti_valid", ltiRequest.isComplete()); 
        httpServletRequest.setAttribute(LMSRequest.class.getName(), ltiRequest); 
        super.doFilter(servletRequest, servletResponse, chain);
    }

}
