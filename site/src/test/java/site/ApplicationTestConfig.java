package site;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.hornetq.HornetQAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.DeviceDelegatingViewResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.DeviceResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.SitePreferenceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration;
import org.springframework.boot.autoconfigure.security.FallbackWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityDataConfiguration;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import util.profiles.Profiles;

@Profile(Profiles.TEST)
@SpringBootApplication
@ComponentScan(basePackages = { "site" }, excludeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, value = { Application.class }) })
@EnableAutoConfiguration(exclude = { HypermediaAutoConfiguration.class,
		RepositoryRestMvcAutoConfiguration.class,
		ActiveMQAutoConfiguration.class,
		AopAutoConfiguration.CglibAutoProxyConfiguration.class,
		ArtemisAutoConfiguration.class, BatchAutoConfiguration.class,
		CacheAutoConfiguration.class, CassandraAutoConfiguration.class,
		CassandraDataAutoConfiguration.class,
		CassandraRepositoriesAutoConfiguration.class,
		CloudAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		DeviceDelegatingViewResolverAutoConfiguration.class,
		DeviceResolverAutoConfiguration.class,
		ElasticsearchAutoConfiguration.class,
		ElasticsearchDataAutoConfiguration.class,
		ElasticsearchRepositoriesAutoConfiguration.class,
		EmbeddedMongoAutoConfiguration.class,
		EmbeddedServletContainerAutoConfiguration.EmbeddedJetty.class,
		EmbeddedServletContainerAutoConfiguration.EmbeddedUndertow.class,
		FallbackWebSecurityAutoConfiguration.class,
		FlywayAutoConfiguration.FlywayConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		GroovyTemplateAutoConfiguration.class, GsonAutoConfiguration.class,
		HazelcastAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		HornetQAutoConfiguration.class, InfinispanCacheConfiguration.class,
		IntegrationAutoConfiguration.class, JerseyAutoConfiguration.class,
		JmsAutoConfiguration.class,
		JndiConnectionFactoryAutoConfiguration.class,
		JndiDataSourceAutoConfiguration.class,
		JooqAutoConfiguration.DslContextConfiguration.class,
		JpaRepositoriesAutoConfiguration.class, JtaAutoConfiguration.class,
		LiquibaseAutoConfiguration.class, MailSenderAutoConfiguration.class,
		MailSenderValidatorAutoConfiguration.class,
		MessageSourceAutoConfiguration.class, MongoAutoConfiguration.class,
		MongoDataAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class,
		MustacheAutoConfiguration.class, OAuth2AutoConfiguration.class,
		RabbitAutoConfiguration.class, ReactorAutoConfiguration.class,
		RepositoryRestMvcAutoConfiguration.class,
		SecurityDataConfiguration.class, SendGridAutoConfiguration.class,
		SitePreferenceAutoConfiguration.class, SolrAutoConfiguration.class,
		SolrRepositoriesAutoConfiguration.class,
		SpringApplicationAdminJmxAutoConfiguration.class,
		SpringBootWebSecurityConfiguration.class,
		SpringDataWebAutoConfiguration.class, VelocityAutoConfiguration.class,
		WebSocketMessagingAutoConfiguration.class,
		XADataSourceAutoConfiguration.class })
@PropertySource("classpath:/application-test.properties")
public class ApplicationTestConfig implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
