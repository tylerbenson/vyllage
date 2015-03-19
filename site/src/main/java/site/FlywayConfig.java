package site;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Since we're running multiple applications in the same JVM, we need to
 * initialize each apps database separately.
 * 
 * TODO: It'd be nice if we could do this more programmatically with less hard
 * coding.
 */
@Configuration
public class FlywayConfig {
	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment env;

	@Bean(initMethod = "migrate")
	public Flyway flywayConnections() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(this.dataSource);
		flyway.setLocations(env.getRequiredProperty("flyway.connections"));
		flyway.setSchemas("CONNECTIONS");
		return flyway;
	}

	@Bean(initMethod = "migrate")
	public Flyway flywayDocuments() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(this.dataSource);
		flyway.setLocations(env.getRequiredProperty("flyway.documents"));
		flyway.setSchemas("DOCUMENTS");
		return flyway;
	}

	@Bean(initMethod = "migrate")
	public Flyway flywayAccounts() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(this.dataSource);
		flyway.setLocations(env.getRequiredProperty("flyway.accounts"));
		flyway.setSchemas("ACCOUNTS");
		return flyway;
	}
}
