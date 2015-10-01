package documents.model.document.sections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import documents.model.constants.SectionType;

public class DocumentSectionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testSetType() {
		DocumentSection ds = new AchievementsSection();
		ds.setType("asdasd");
	}

	@Test
	public void testfromJSONFails() {

		assertTrue(DocumentSection.fromJSON("{ ") == null);
	}

	@Test
	public void testEquals() {
		DocumentSection ds1 = new AchievementsSection();
		ds1.setType(SectionType.PROJECTS_SECTION.type());

		DocumentSection ds2 = new AchievementsSection();
		ds2.setType(SectionType.PROJECTS_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsNullFalse() {
		DocumentSection ds1 = new AchievementsSection();
		ds1.setType(SectionType.PROJECTS_SECTION.type());

		assertFalse(ds1.equals(null));
	}
}
