package login.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource datasource;

	// http://stackoverflow.com/questions/21231057/how-to-configure-spring-4-0-with-spring-boot-and-spring-security-openid?rq=1
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/css/**", "/images/**", "/javascript/**")
				.permitAll().anyRequest().authenticated();

		http.formLogin().defaultSuccessUrl("/resume/")
				.usernameParameter("email").loginPage("/login").permitAll();

		http.logout().permitAll();

		http.csrf().disable();

	}
}
