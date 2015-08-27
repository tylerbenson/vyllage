package oauth.lti;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import oauth.model.service.LMSOAuthNonceServices;
import oauth.utilities.LMSConstants;

import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.CoreOAuthProviderSupport;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;

public class LMSOAuthProviderProcessingFilter extends
		ProtectedResourceProcessingFilter {

	private boolean isProd;

	public LMSOAuthProviderProcessingFilter(
			LMSConsumerDetailsService oAuthConsumerDetailsService,
			LMSOAuthNonceServices oAuthNonceServices,
			OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint,
			LMSOAuthAuthenticationHandler oAuthAuthenticationHandler,
			OAuthProviderTokenServices oAuthProviderTokenServices,
			boolean isProd) {
		super();
		this.isProd = isProd;
		setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
		setAuthHandler(oAuthAuthenticationHandler);
		setConsumerDetailsService(oAuthConsumerDetailsService);
		setNonceServices(oAuthNonceServices);
		setTokenServices(oAuthProviderTokenServices);

	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		if (!(servletRequest instanceof HttpServletRequest)) {
			throw new IllegalStateException(
					LMSConstants.LTI_INVALID_HTTPSER_REQ);
		}
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		LMSRequest ltiRequest = new LMSRequest(httpServletRequest);
		httpServletRequest.setAttribute("LTI", true);
		httpServletRequest.setAttribute("lti_valid", ltiRequest.isComplete());
		httpServletRequest.setAttribute(LMSRequest.class.getName(), ltiRequest);
		if (isProd) {
			// It would be nice if we could tell this from request info,
			// perhaps some provided by AWS... but for now lets get it working.
			if (getProviderSupport() instanceof CoreOAuthProviderSupport)
				((CoreOAuthProviderSupport) getProviderSupport())
						.setBaseUrl("https://"
								+ httpServletRequest.getServerName());
		}
		super.doFilter(servletRequest, servletResponse, chain);
	}

}
