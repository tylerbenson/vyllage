package site;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

	@Bean(initMethod = "migrate")
	public Flyway flywayConnections() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(this.dataSource);
		flyway.setLocations("classpath:connections/db/migration");
		flyway.setSchemas("CONNECTIONS");
		return flyway;
	}

	@Bean(initMethod = "migrate")
	public Flyway flywayDocuments() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(this.dataSource);
		flyway.setLocations("classpath:documents/db/migration");
		flyway.setSchemas("DOCUMENTS");
		return flyway;
	}

	@Bean(initMethod = "migrate")
	public Flyway flywayAccounts() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(this.dataSource);
		flyway.setLocations("classpath:accounts/db/migration");
		flyway.setSchemas("ACCOUNTS");
		return flyway;
	}
}
