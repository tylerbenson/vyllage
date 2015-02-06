package editor;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import editor.domain.public_.tables.Accounts;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testUserNotFound() {
		DSLContext sql = DSL.using(dataSource, SQLDialect.H2);

		Result<Record> records = sql.select().from(Accounts.ACCOUNTS)
				.where(Accounts.ACCOUNTS.USERNAME.eq("invalidUser")).fetch();

		assertEquals(records.isEmpty(), true);
	}

	@Test
	public void testUserFound() {
		DSLContext sql = DSL.using(dataSource, SQLDialect.H2);

		Result<Record> records = sql.select().from(Accounts.ACCOUNTS)
				.where(Accounts.ACCOUNTS.USERNAME.eq("username")).fetch();

		assertEquals(records.isEmpty(), false);
	}

}
