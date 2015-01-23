package login.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class AuthenticationSecurity extends
		GlobalAuthenticationConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	// @Override
	// public void init(AuthenticationManagerBuilder auth) throws Exception {
	// // auth.jdbcAuthentication().dataSource(dataSource);
	// auth.inMemoryAuthentication().withUser("user").password("password")
	// .roles("USER");
	// }

	// http://stackoverflow.com/questions/24389563/spring-security-with-spring-boot-configuration
	// https://github.com/spring-projects/spring-boot/blob/master/spring-boot-samples/spring-boot-sample-web-method-security/src/main/java/sample/ui/method/SampleMethodSecurityApplication.java
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		JdbcUserDetailsManager userDetailsService = jdbcUserService();
		auth.userDetailsService(userDetailsService).passwordEncoder(
				new BCryptPasswordEncoder());
		// auth.jdbcAuthentication().dataSource(dataSource);
		auth.inMemoryAuthentication().withUser("user").password("password")
				.roles("USER");
	}

	@Bean
	public org.springframework.security.provisioning.JdbcUserDetailsManager jdbcUserService()
			throws Exception {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		jdbcUserDetailsManager.setDataSource(dataSource);
		// jdbcUserDetailsManager.setAuthenticationManager(authenticationManagerBean());
		return jdbcUserDetailsManager;
	}
}