package editor;

import static editor.domain.editor.tables.Accounts.ACCOUNTS;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class JooqTest {

	@Autowired
	DSLContext sql;

	@Test
	public void testUserNotFound() {
		// DSLContext sql = DSL.using(dataSource, SQLDialect.H2);
		Result<Record> records = sql.select().from(ACCOUNTS)
				.where(ACCOUNTS.USERNAME.eq("invalidUser")).fetch();

		assertEquals(records.isEmpty(), true);
	}

	@Test
	public void testUserFound() {
		Result<Record> records = sql.select().from(ACCOUNTS)
				.where(ACCOUNTS.USERNAME.eq("username")).fetch();

		assertEquals(records.isEmpty(), false);
	}

	@Test
	public void testInsertUser() {
		sql.insertInto(ACCOUNTS, ACCOUNTS.USERNAME).values("test").execute();

		Result<Record> records = sql.select().from(ACCOUNTS)
				.where(ACCOUNTS.USERNAME.eq("test")).fetch();

		assertEquals("User not found ", records.isEmpty(), false);
		assertEquals(records.get(0).getValue(ACCOUNTS.USERNAME), "test");
	}

	@Test
	public void testUpdateUser() {
		sql.insertInto(ACCOUNTS, ACCOUNTS.USERNAME).values("test").execute();

		sql.update(ACCOUNTS).set(ACCOUNTS.USERNAME, "updated")
				.where(ACCOUNTS.USERNAME.eq("test")).execute();

		Result<Record> records = sql.select().from(ACCOUNTS)
				.where(ACCOUNTS.USERNAME.eq("test")).fetch();

		Result<Record> updatedRecords = sql.select().from(ACCOUNTS)
				.where(ACCOUNTS.USERNAME.eq("updated")).fetch();

		assertEquals("User not udated ", records.isEmpty(), true);
		assertEquals(updatedRecords.get(0).getValue(ACCOUNTS.USERNAME),
				"updated");
	}

	@Test
	public void testDeleteUser() {
		sql.insertInto(ACCOUNTS, ACCOUNTS.USERNAME).values("test").execute();

		sql.delete(ACCOUNTS).where(ACCOUNTS.USERNAME.eq("test")).execute();

		Result<Record> records = sql.select().from(ACCOUNTS)
				.where(ACCOUNTS.USERNAME.eq("test")).fetch();

		assertEquals("User not deleted ", records.isEmpty(), true);
	}

}
