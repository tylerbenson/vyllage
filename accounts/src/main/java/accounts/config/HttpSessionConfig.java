package accounts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableRedisHttpSession
@Profile(value = "prod")
// TODO: change to prod.
public class HttpSessionConfig {

}
