package documents.model;

import org.junit.Assert;
import org.junit.Test;

import documents.model.constants.DocumentAccessEnum;

public class DocumentAccessTest {

	@Test
	public void checkReadOnRead() {
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);

		Assert.assertTrue(documentAccess.checkAccess(DocumentAccessEnum.READ));
	}

	@Test
	public void checkReadOnWrite() {
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.WRITE);

		// if we can write then we can also read.
		Assert.assertTrue(documentAccess.checkAccess(DocumentAccessEnum.READ));
	}

	@Test
	public void checkWriteOnWrite() {
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.WRITE);

		Assert.assertTrue(documentAccess.checkAccess(DocumentAccessEnum.WRITE));
	}

	@Test
	public void checkWriteOnRead() {
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);

		Assert.assertFalse(documentAccess.checkAccess(DocumentAccessEnum.WRITE));
	}
}
