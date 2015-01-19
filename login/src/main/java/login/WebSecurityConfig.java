package login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
// @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().permitAll();
		// http.authorizeRequests().antMatchers("/", "/home").permitAll()
		// .anyRequest().authenticated().and().formLogin()
		// .loginPage("/login").permitAll().and().logout().permitAll();
		// http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin()
		// .loginPage("/login").failureUrl("/login?error").permitAll();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		// auth.inMemoryAuthentication().withUser("admin").password("admin")
		// .roles("ADMIN", "USER").and().withUser("user").password("user")
		// .roles("USER");
		auth.inMemoryAuthentication().withUser("user").password("password")
				.roles("USER");
	}
}