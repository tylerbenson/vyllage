package accounts.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import oauth.lti.LMSConsumerDetailsService;
import oauth.lti.LMSOAuthAuthenticationHandler;
import oauth.lti.LMSOAuthProviderProcessingFilter;
import oauth.model.service.LMSOAuthNonceServices;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
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
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class })
@EnableCaching
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(1)
public class LTISecurityConfigurer extends WebSecurityConfigurerAdapter {

	private LMSOAuthProviderProcessingFilter ltioAuthProviderProcessingFilter;

	@Inject
	private LMSConsumerDetailsService lmsConsumerDetailsService;

	@Inject
	private LMSOAuthNonceServices lmsOauthNonceServices;

	@Inject
	private LMSOAuthAuthenticationHandler lmsOauthAuthenticationHandler;

	@Inject
	private OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;

	@Inject
	private OAuthProviderTokenServices oauthProviderTokenServices;

	@Inject
	private Environment env;

	@PostConstruct
	public void init() {
		ltioAuthProviderProcessingFilter = new LMSOAuthProviderProcessingFilter(
				lmsConsumerDetailsService, lmsOauthNonceServices,
				oauthProcessingFilterEntryPoint, lmsOauthAuthenticationHandler,
				oauthProviderTokenServices, env.acceptsProfiles("prod"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.requestMatchers()
				.antMatchers("/lti/account")
				.and()
				.addFilterBefore(ltioAuthProviderProcessingFilter,
						UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().anyRequest().hasRole("LTI").and().csrf()
				.disable();

		http.authorizeRequests().antMatchers("/lti/login").permitAll();

		http.headers().disable();
		http.authorizeRequests().anyRequest().authenticated();

		// http.antMatcher("/**").authorizeRequests().anyRequest().permitAll();
		// http.headers().disable();
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
