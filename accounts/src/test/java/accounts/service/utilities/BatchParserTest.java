package accounts.service.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import accounts.service.utilities.BatchParser.ParsedAccount;

public class BatchParserTest {

	@Test
	public void parseSingleLineTest() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = "unemail@email.com, First Name, Middle Name, Last Name";

		List<ParsedAccount> parsed = parser.parse(txt);
		assertNotNull(parsed);
		assertFalse(parsed.isEmpty());
		assertEquals("unemail@email.com", parsed.get(0).getEmail());
		assertEquals("First Name", parsed.get(0).getFirstName());
		assertEquals("Middle Name", parsed.get(0).getMiddleName());
		assertEquals("Last Name", parsed.get(0).getLastName());
	}

	@Test
	public void parseSingleLineMissingFieldsTest() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = "unemail@email.com,,,";

		List<ParsedAccount> parsed = parser.parse(txt);
		assertNotNull(parsed);
		assertFalse(parsed.isEmpty());
		assertEquals("unemail@email.com", parsed.get(0).getEmail());
		assertEquals(null, parsed.get(0).getFirstName());
		assertEquals(null, parsed.get(0).getMiddleName());
		assertEquals(null, parsed.get(0).getLastName());
	}

	@Test
	public void parseMultipleLinesTest() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = "email@email.com, First Name, Middle Name, Last Name\nemail2@email.com, First Name2, Middle Name2, Last Name2";

		List<ParsedAccount> parsed = parser.parse(txt);
		assertNotNull(parsed);
		assertFalse(parsed.isEmpty());
		assertEquals("email2@email.com", parsed.get(1).getEmail());
		assertEquals("First Name2", parsed.get(1).getFirstName());
		assertEquals("Middle Name2", parsed.get(1).getMiddleName());
		assertEquals("Last Name2", parsed.get(1).getLastName());
	}

	@Test
	public void parseMultipleLinesTestWithMissingFields() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = "email@email.com, First Name,,\nemail2@email.com,, Middle Name2,";

		List<ParsedAccount> parsed = parser.parse(txt);
		assertNotNull(parsed);
		assertFalse(parsed.isEmpty());
		assertEquals("email@email.com", parsed.get(0).getEmail());
		assertEquals("First Name", parsed.get(0).getFirstName());
		assertTrue(parsed.get(0).getMiddleName() == null);
		assertTrue(parsed.get(0).getLastName() == null);

		assertEquals("email2@email.com", parsed.get(1).getEmail());
		assertTrue(parsed.get(1).getFirstName() == null);
		assertEquals("Middle Name2", parsed.get(1).getMiddleName());
		assertTrue(parsed.get(1).getLastName() == null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseNullArgument() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = null;

		parser.parse(txt);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseEmptyArgument() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = "";

		parser.parse(txt);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseInvalidEmail() throws IOException {
		BatchParser parser = new BatchParser();
		String txt = "@email.com, First Name,,\nemail2@email.com,, Middle Name2,";

		parser.parse(txt);
	}
}
