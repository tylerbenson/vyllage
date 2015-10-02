package documents.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jooq.tools.StringUtils;
import org.junit.Test;

public class DocumentHeaderTest {

	// 101
	private static final String TOO_LONG_TAGLINE = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
			+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";

	@Test
	public void testIsInValid() {
		DocumentHeader dh = new DocumentHeader();

		dh.setTagline(TOO_LONG_TAGLINE);

		assertTrue(dh.isInValid());
	}

	@Test
	public void testTaglineIsInValid() {
		DocumentHeader dh = new DocumentHeader();

		dh.setTagline(TOO_LONG_TAGLINE);

		assertTrue(dh.tagLineIsInValid());
	}

	@Test
	public void testTaglineIsValid() {
		DocumentHeader dh = new DocumentHeader();

		// 100
		dh.setTagline("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

		assertFalse(dh.tagLineIsInValid());
	}

	@Test
	public void testEquals() {
		DocumentHeader dh1 = new DocumentHeader();
		dh1.setAddress("address");

		DocumentHeader dh2 = new DocumentHeader();
		dh2.setAddress("address");

		assertTrue(dh1.equals(dh2));

	}

	@Test
	public void testEqualsNull() {
		DocumentHeader dh1 = new DocumentHeader();
		dh1.setAddress("address");

		assertFalse(dh1.equals(null));

	}

	@Test
	public void testToString() {
		DocumentHeader dh1 = new DocumentHeader();
		dh1.setAddress("address");

		String string = dh1.toString();
		assertFalse(string == null || StringUtils.isBlank(string));

	}

}
