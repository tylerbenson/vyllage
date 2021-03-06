package oauth.model.service;

import oauth.lti.LMSConsumerDetailsService;

import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;

public class ZeroLeggedOAuthProviderProcessingFilter extends
		ProtectedResourceProcessingFilter {

	public ZeroLeggedOAuthProviderProcessingFilter(
			LMSConsumerDetailsService oAuthConsumerDetailsService,
			LMSOAuthNonceServices oAuthNonceServices,
			OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint,
			OAuthAuthenticationHandler oAuthAuthenticationHandler,
			OAuthProviderTokenServices oAuthProviderTokenServices) {
		super();
		setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
		setAuthHandler(oAuthAuthenticationHandler);
		setConsumerDetailsService(oAuthConsumerDetailsService);
		setNonceServices(oAuthNonceServices);
		setTokenServices(oAuthProviderTokenServices);
	}
}
