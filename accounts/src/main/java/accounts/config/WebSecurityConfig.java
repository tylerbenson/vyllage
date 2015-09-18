package accounts.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import accounts.config.beans.RequestMatcherDisable;
import accounts.config.handlers.ConfirmEmailAddressSuccessHandler;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource datasource;

	// http://stackoverflow.com/questions/21231057/how-to-configure-spring-4-0-with-spring-boot-and-spring-security-openid?rq=1
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/lti/login", "/account/reset-password",
						"/account/reset-password-change/**",
						"account/password-change-success", "/signin/**",
						"/signup/**", "/social-login/**", "/link/e/**",
						"/link/s/**", "/register", "/register-from-social",
						"/register-from-LTI", "/careers", "/privacy",
						"/contact", "/lti/login-existing-user", "/lti/rss",
						"/robots.txt").permitAll();

		http.authorizeRequests()
				.antMatchers("/swagger-ui.html", "/v2/api-docs/**",
						"/swagger-resources").hasAuthority("ADMIN");

		// disabling CSRF for the togglz console.
		http.csrf().requireCsrfProtectionMatcher(new RequestMatcherDisable());

		// Allow frames to enable the h2 console.
		http.headers().addHeaderWriter(
				new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
		http.headers().disable();
		http.authorizeRequests()
				.antMatchers("/", "/status", "/status-*", "/css/**",
						"/images/**", "/javascript/**").permitAll();
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
	public ConfirmEmailAddressSuccessHandler successHandler() {

		ConfirmEmailAddressSuccessHandler confirmEmailAddressSuccessHandler = new ConfirmEmailAddressSuccessHandler();
		confirmEmailAddressSuccessHandler.setDefaultTargetUrl("/resume/");

		return confirmEmailAddressSuccessHandler;
	}

}
