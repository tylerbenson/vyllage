package documents;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeansConfiguration {
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	// @Bean
	// public PermissionEvaluator permissionEvaluator(AclService aclService) {
	// return new AclPermissionEvaluator(aclService);
	// }
	//
	// @Bean
	// public DefaultMethodSecurityExpressionHandler
	// defaultMethodSecurityExpressionHandler(
	// PermissionEvaluator permissionEvaluator) {
	// DefaultMethodSecurityExpressionHandler exp = new
	// DefaultMethodSecurityExpressionHandler();
	// exp.setPermissionEvaluator(permissionEvaluator);
	// return exp;
	// }

}
