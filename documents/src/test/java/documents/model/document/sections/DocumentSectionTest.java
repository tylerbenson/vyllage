package documents.model.document.sections;

import org.junit.Assert;
import org.junit.Test;

public class DocumentSectionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testSetType() {
		DocumentSection ds = new AchievementsSection();
		ds.setType("asdasd");
	}

	@Test()
	public void testfromJSONFails() {

		Assert.assertTrue(DocumentSection.fromJSON("{ ") == null);
	}
}
