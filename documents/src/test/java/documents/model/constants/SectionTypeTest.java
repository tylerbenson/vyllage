package documents.model.constants;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SectionTypeTest {

	@Test
	public void testIsValid() {
		assertTrue(SectionType.isValidType("JobExperienceSection"));
	}

	@Test
	public void testIsNotValid() {
		assertFalse(SectionType.isValidType("No."));
	}
}
