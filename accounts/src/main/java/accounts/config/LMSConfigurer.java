package accounts.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import oauth.lti.LMSConsumerDetailsService;
import oauth.lti.LMSOAuthAuthenticationHandler;
import oauth.lti.LMSOAuthProviderProcessingFilter;
import oauth.model.service.LMSAuthenticationHandler;
import oauth.model.service.LMSOAuthNonceServices;
import oauth.model.service.ZeroLeggedOAuthProviderProcessingFilter;

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement 
@EnableCaching 
@EnableWebMvcSecurity 
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class LMSConfigurer extends WebMvcConfigurerAdapter {

    final static Logger log = LoggerFactory.getLogger(LMSConfigurer.class);


    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(); 
    }


    @Configuration
    public static class LTISecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
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

        @PostConstruct
        public void init() {
            ltioAuthProviderProcessingFilter = new LMSOAuthProviderProcessingFilter( lmsConsumerDetailsService, lmsOauthNonceServices, oauthProcessingFilterEntryPoint, lmsOauthAuthenticationHandler, oauthProviderTokenServices);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
        	http.headers().disable() ;
            http.requestMatchers().antMatchers("/lti/**").and().addFilterBefore(ltioAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class).authorizeRequests().anyRequest().hasRole("LTI").and().csrf().disable();
        }
    }

    @Configuration
    public static class OAuthSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private ZeroLeggedOAuthProviderProcessingFilter zeroLeggedOAuthProviderProcessingFilter;
        @Autowired
        LMSConsumerDetailsService oauthConsumerDetailsService;
        @Autowired
        LMSOAuthNonceServices oauthNonceServices;
        @Autowired
        LMSAuthenticationHandler authenticationHandler;
        @Autowired
        OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint;
        @Autowired
        OAuthProviderTokenServices oauthProviderTokenServices;

        @PostConstruct
        public void init() {
            zeroLeggedOAuthProviderProcessingFilter = new ZeroLeggedOAuthProviderProcessingFilter(oauthConsumerDetailsService, oauthNonceServices, oauthProcessingFilterEntryPoint, authenticationHandler, oauthProviderTokenServices);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
        	http.headers().disable() ;
            http.antMatcher("/oauth/**")
                    .addFilterBefore(zeroLeggedOAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests().anyRequest().hasRole("OAUTH")
                    .and().csrf().disable(); 
        }
    }

    @Bean(name = "oauthProviderTokenServices")
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        return new InMemoryProviderTokenServices();
    }
}
