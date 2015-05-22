package site;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Site-wide MVC infrastructure configuration. See also {@link SiteConfig} where
 * certain additional web infrastructure is configured.
 */
@Configuration(value = "site.MvcConfig")
class MvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private PrincipalDetailsInterceptor principalDetailsInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(principalDetailsInterceptor);
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	}
}

@Configuration
@Profile(Profiles.DEV)
class ClientResourcesConfig extends WebMvcConfigurerAdapter {

	@Value("${PROJECT_HOME:}")
	private String homePath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!this.homePath.isEmpty()) {
			registry.addResourceHandler("/**")
					.addResourceLocations(
							"file:///" + this.homePath + "/assets/public/")
					.setCachePeriod(0);
		}
	}
}