package site;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.base.Predicate;

@Configuration(value = "accounts.config.SwaggerConfig")
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket documentsApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("documents")
				.select().paths(documentsPaths()).build().apiInfo(apiInfo());
	}

	protected Predicate<String> documentsPaths() {
		return or(regex("/resume/.*"), regex("/document/.*"));
	}

	@Bean
	public Docket accountsApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("accounts")
				.select().paths(accountsPaths()).build().apiInfo(apiInfo());
	}

	@SuppressWarnings("unchecked")
	protected Predicate<String> accountsPaths() {
		return or(regex("/admin/.*"), regex("/account/.*"),
				regex("/register/.*"));
	}

	@Bean
	public Docket socialApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("social")
				.select().paths(socialPaths()).build().apiInfo(apiInfo());
	}

	protected Predicate<String> socialPaths() {
		return or(regex("/connect/.*"), regex("/disconnect/.*"));
	}

	@Bean
	public Docket ltiApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("lti")
				.select().paths(ltiPaths()).build().apiInfo(apiInfo());
	}

	protected Predicate<String> ltiPaths() {
		return regex("/lti/.*");
	}

	@Bean
	public Docket allApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("all")
				.select().paths(allPaths()).build().apiInfo(apiInfo());
	}

	protected Predicate<String> allPaths() {
		return regex("/.*");
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("Vyllage's REST API",
				"This is a description of our API.", "API TOS",
				"admin@vyllage.com", "Vyllage", "API License URL",
				"www.vyllage.com");
		return apiInfo;
	}

	@Bean
	public UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl");
	}
}
