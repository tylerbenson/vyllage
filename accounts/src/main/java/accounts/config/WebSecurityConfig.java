package accounts.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import oauth.lti.LMSConsumerDetailsService;
import oauth.lti.LMSOAuthAuthenticationHandler;
import oauth.lti.LMSOAuthProviderProcessingFilter;
import oauth.model.service.LMSAuthenticationHandler;
import oauth.model.service.LMSOAuthNonceServices;
import oauth.model.service.ZeroLeggedOAuthProviderProcessingFilter;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Configuration
@EnableWebMvcSecurity
@ComponentScan(basePackages = {"oauth.lti", "oauth.model"})
@EnableAutoConfiguration
@EnableCaching 
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource datasource;
	
	private LMSOAuthProviderProcessingFilter ltioAuthProviderProcessingFilter;
    @Autowired
    LMSConsumerDetailsService lmsConsumerDetailsService;
    @Autowired
    LMSOAuthNonceServices lmsOauthNonceServices;
    @Autowired
    LMSOAuthAuthenticationHandler lmsOauthAuthenticationHandler;
    @Autowired
    OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
    @Autowired
    OAuthProviderTokenServices oauthProviderTokenServices;
    
    private ZeroLeggedOAuthProviderProcessingFilter zeroLeggedOAuthProviderProcessingFilter;
    @Autowired
    LMSConsumerDetailsService oauthConsumerDetailsService;
    @Autowired
    LMSOAuthNonceServices oauthNonceServices;
    @Autowired
    LMSAuthenticationHandler authenticationHandler;
    @Autowired
    OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint2;
    @Autowired
    OAuthProviderTokenServices oauthProviderTokenServices2;
    
    @PostConstruct
    public void init() {
        ltioAuthProviderProcessingFilter = new LMSOAuthProviderProcessingFilter( 
        			lmsConsumerDetailsService, lmsOauthNonceServices, oauthProcessingFilterEntryPoint, 
        			lmsOauthAuthenticationHandler, oauthProviderTokenServices);
        zeroLeggedOAuthProviderProcessingFilter = new ZeroLeggedOAuthProviderProcessingFilter(
        			oauthConsumerDetailsService, oauthNonceServices, oauthProcessingFilterEntryPoint2, 
        			authenticationHandler, oauthProviderTokenServices2);
    }
    
	// http://stackoverflow.com/questions/21231057/how-to-configure-spring-4-0-with-spring-boot-and-spring-security-openid?rq=1
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/account/reset-password",
						"/account/reset-password-change/**", "/signin/**",
						"/signup/**", "/social-login/**", "/link/e/**",	"/link/s/**").permitAll();

		// disabling CSRF for the togglz console.
		http.csrf().requireCsrfProtectionMatcher(
				new TogglzConsoleRequestMatcher());

		// Allow frames to enable the h2 console.
		http.headers().addHeaderWriter(
				new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));

		http.authorizeRequests()
				.antMatchers("/", "/status", "/status-*", "/css/**",
						"/images/**", "/javascript/**").permitAll();
		
		http.headers().disable() ;
        http.requestMatchers().antMatchers("/lti/**").and()
        		.addFilterBefore(ltioAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
        		.authorizeRequests().anyRequest().hasRole("LTI").and().csrf().disable();
        http.antMatcher("/oauth/**")
        	.addFilterBefore(zeroLeggedOAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
        	.authorizeRequests().anyRequest().hasRole("OAUTH")
        	.and().csrf().disable(); 
        
		http.authorizeRequests().anyRequest().authenticated();

		SimpleUrlAuthenticationSuccessHandler successHandler = successHandler();
		successHandler.setAlwaysUseDefaultTargetUrl(true);
		http.formLogin().loginPage("/login").usernameParameter("email")
				.defaultSuccessUrl("/resume/").successHandler(successHandler)
				.permitAll();

		http.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.permitAll();

	}

	@Bean
	public AddUserAuthenticationSuccessHandler successHandler() {
		AddUserAuthenticationSuccessHandler successHandler = new AddUserAuthenticationSuccessHandler();
		successHandler.setDefaultTargetUrl("/resume/");

		return successHandler;
	}
	
	@Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(); 
    }
	
	@Bean(name = "oauthProviderTokenServices")
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        return new InMemoryProviderTokenServices();
    }
}
