package accounts.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import oauth.lti.LMSConsumerDetailsService;
import oauth.model.service.LMSAuthenticationHandler;
import oauth.model.service.LMSOAuthNonceServices;
import oauth.model.service.ZeroLeggedOAuthProviderProcessingFilter;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@ComponentScan({ "oauth.lti", "oauth.model.service" })
@Configuration
@EnableAutoConfiguration
@EnableCaching
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(11)
public class OAuthSecurityConfigurer extends WebSecurityConfigurerAdapter {
	private ZeroLeggedOAuthProviderProcessingFilter zeroLeggedOAuthProviderProcessingFilter;
	@Inject
	LMSConsumerDetailsService oauthConsumerDetailsService;
	@Inject
	LMSOAuthNonceServices oauthNonceServices;
	@Inject
	LMSAuthenticationHandler authenticationHandler;
	@Inject
	OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
	@Inject
	OAuthProviderTokenServices oauthProviderTokenServices;

	@PostConstruct
	public void init() {
		zeroLeggedOAuthProviderProcessingFilter = new ZeroLeggedOAuthProviderProcessingFilter(
				oauthConsumerDetailsService, oauthNonceServices,
				oauthProcessingFilterEntryPoint, authenticationHandler,
				oauthProviderTokenServices);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().disable();
		http.antMatcher("/oauth/**")
				.addFilterBefore(zeroLeggedOAuthProviderProcessingFilter,
						UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().anyRequest().hasRole("OAUTH").and().csrf()
				.disable();
	}

	@Bean(name = "oauthProviderTokenService")
	public OAuthProviderTokenServices oauthProviderTokenServices() {
		return new InMemoryProviderTokenServices();
	}

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}
}
