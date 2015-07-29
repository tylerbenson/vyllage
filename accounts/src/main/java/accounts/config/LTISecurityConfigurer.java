package accounts.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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

import oauth.lti.LMSConsumerDetailsService;
import oauth.lti.LMSOAuthAuthenticationHandler;
import oauth.lti.LMSOAuthProviderProcessingFilter;
import oauth.model.service.LMSOAuthNonceServices;

@ComponentScan({ "oauth.lti", "oauth.model.service" })
@Configuration
@EnableAutoConfiguration
@EnableCaching
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(1)
public class LTISecurityConfigurer extends WebSecurityConfigurerAdapter {

	private LMSOAuthProviderProcessingFilter ltioAuthProviderProcessingFilter;
	private final LMSConsumerDetailsService lmsConsumerDetailsService;
	private final LMSOAuthNonceServices lmsOauthNonceServices;
	private final LMSOAuthAuthenticationHandler lmsOauthAuthenticationHandler;
	private final OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
	private final OAuthProviderTokenServices oauthProviderTokenServices;

	@Inject
	public LTISecurityConfigurer(final LMSConsumerDetailsService lmsConsumerDetailsService,
			final LMSOAuthNonceServices lmsOauthNonceServices,
			final LMSOAuthAuthenticationHandler lmsOauthAuthenticationHandler,
			final OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint,
			final OAuthProviderTokenServices oauthProviderTokenServices) {
		this.lmsConsumerDetailsService = lmsConsumerDetailsService;
		this.lmsOauthNonceServices = lmsOauthNonceServices;
		this.lmsOauthAuthenticationHandler = lmsOauthAuthenticationHandler;
		this.oauthProcessingFilterEntryPoint = oauthProcessingFilterEntryPoint;
		this.oauthProviderTokenServices = oauthProviderTokenServices;
	}

	@PostConstruct
	public void init() {
		ltioAuthProviderProcessingFilter = new LMSOAuthProviderProcessingFilter(lmsConsumerDetailsService,
				lmsOauthNonceServices, oauthProcessingFilterEntryPoint, lmsOauthAuthenticationHandler,
				oauthProviderTokenServices);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().disable();
		http.requestMatchers().antMatchers("/lti/**").and()
				.addFilterBefore(ltioAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().anyRequest().hasRole("LTI").and().csrf().disable();
	}

	@Bean(name = "oauthProviderTokenServices")
	public OAuthProviderTokenServices oauthProviderTokenServices() {
		return new InMemoryProviderTokenServices();
	}

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}
}
