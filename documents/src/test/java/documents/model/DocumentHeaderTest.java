package documents.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
