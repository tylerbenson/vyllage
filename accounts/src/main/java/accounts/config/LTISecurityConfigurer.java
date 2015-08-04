package accounts.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
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
	@Inject
	LMSConsumerDetailsService lmsConsumerDetailsService;
	@Inject
	LMSOAuthNonceServices lmsOauthNonceServices;
	@Inject
	LMSOAuthAuthenticationHandler lmsOauthAuthenticationHandler;
	@Inject
	OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
	@Inject
	OAuthProviderTokenServices oauthProviderTokenServices;

	@PostConstruct
	public void init() {
		ltioAuthProviderProcessingFilter = new LMSOAuthProviderProcessingFilter(lmsConsumerDetailsService,
				lmsOauthNonceServices, oauthProcessingFilterEntryPoint, lmsOauthAuthenticationHandler,
				oauthProviderTokenServices);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.requestMatchers().antMatchers("/lti/account").and()
				.addFilterBefore(ltioAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().anyRequest().hasRole("LTI").and().csrf().disable();

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
