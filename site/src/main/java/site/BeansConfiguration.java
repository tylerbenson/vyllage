package site;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(value = "site.BeansConfiguration")
public class BeansConfiguration {
	@Bean
	@Profile(Profiles.DEV)
	public ServletRegistrationBean h2Console() {
		ServletRegistrationBean reg = new ServletRegistrationBean(
				new WebServlet(), "/db-console/*");
		reg.setLoadOnStartup(1);
		return reg;
	}
}