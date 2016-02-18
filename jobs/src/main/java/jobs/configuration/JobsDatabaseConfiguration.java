package jobs.configuration;

import javax.sql.DataSource;

import jobs.database.ExceptionTranslator;
import jobs.database.SpringTransactionProvider;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.TransactionProvider;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
public class JobsDatabaseConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment env;

	@Bean
	public DSLContext dsl(org.jooq.Configuration config) {
		return new DefaultDSLContext(config);
	}

	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public ConnectionProvider connectionProvider(DataSource dataSource) {
		return new DataSourceConnectionProvider(
				new TransactionAwareDataSourceProxy(dataSource));
	}

	@Bean
	public TransactionProvider transactionProvider() {
		return new SpringTransactionProvider();
	}

	@Bean
	public ExceptionTranslator exceptionTranslator() {
		return new ExceptionTranslator();
	}

	@Bean
	public ExecuteListenerProvider executeListenerProvider(
			ExceptionTranslator exceptionTranslator) {
		return new DefaultExecuteListenerProvider(exceptionTranslator);
	}

	@Bean
	public org.jooq.Configuration jooqConfig(
			ConnectionProvider connectionProvider,
			TransactionProvider transactionProvider,
			ExecuteListenerProvider executeListenerProvider) {
		return new DefaultConfiguration()
				.derive(connectionProvider)
				.derive(transactionProvider)
				.derive(executeListenerProvider)
				.derive(SQLDialect.valueOf(env
						.getRequiredProperty("jooq.sql.dialect")))
				.set(new Settings().withRenderNameStyle(RenderNameStyle.LOWER));
	}

}
