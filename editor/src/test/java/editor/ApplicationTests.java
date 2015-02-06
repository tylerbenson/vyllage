package editor;

import static org.junit.Assert.assertEquals;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import editor.domain.public_.tables.Accounts;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

	@Autowired
	DSLContext sql;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testUserNotFound() {
		// DSLContext sql = DSL.using(dataSource, SQLDialect.H2);
		Result<Record> records = sql.select().from(Accounts.ACCOUNTS)
				.where(Accounts.ACCOUNTS.USERNAME.eq("invalidUser")).fetch();

		assertEquals(records.isEmpty(), true);
	}

	@Test
	public void testUserFound() {
		Result<Record> records = sql.select().from(Accounts.ACCOUNTS)
				.where(Accounts.ACCOUNTS.USERNAME.eq("username")).fetch();

		assertEquals(records.isEmpty(), false);
	}

}
