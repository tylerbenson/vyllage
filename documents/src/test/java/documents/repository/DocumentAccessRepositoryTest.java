package documents.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import documents.ApplicationTestConfig;
import documents.model.DocumentAccess;
import documents.model.constants.DocumentAccessEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentAccessRepositoryTest {

	@Autowired
	private DocumentAccessRepository repository;

	@Test
	public void getEmpty() {
		Optional<DocumentAccess> documentAccess = repository.get(5L, 506L);
		Assert.assertFalse(documentAccess.isPresent());
	}

	@Test
	public void getPermissionsFromUserDocuments() {
		Long userId = 0L;

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User user = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		user.setUserId(userId);

		List<DocumentAccess> fromUserDocuments = repository
				.getFromUserDocuments(user);

		Assert.assertNotNull(fromUserDocuments);
		Assert.assertFalse(fromUserDocuments.isEmpty());
	}

	@Test
	public void createTest() {
		Long userId = 2L;
		Long documentId = 0L;

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);
		documentAccess.setUserId(userId);
		documentAccess.setDocumentId(documentId);

		repository.create(documentAccess);

		Optional<DocumentAccess> optional = repository.get(userId, documentId);
		Assert.assertTrue(optional.isPresent());
	}

	@Test
	public void updateTest() {
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);
		documentAccess.setUserId(42L);
		documentAccess.setDocumentId(0L);

		repository.create(documentAccess);

		documentAccess.setAccess(DocumentAccessEnum.WRITE);

		repository.update(documentAccess);

		Optional<DocumentAccess> updatedDocument = repository.get(
				documentAccess.getUserId(), documentAccess.getDocumentId());

		Assert.assertTrue(updatedDocument.isPresent());
		Assert.assertEquals(DocumentAccessEnum.WRITE, updatedDocument.get()
				.getAccess());

	}

	@Test
	public void deleteTest() {
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);
		documentAccess.setUserId(5L);
		documentAccess.setDocumentId(0L);

		repository.create(documentAccess);

		repository.delete(documentAccess);

		Optional<DocumentAccess> deletedDocument = repository.get(
				documentAccess.getUserId(), documentAccess.getDocumentId());
		Assert.assertFalse(deletedDocument.isPresent());
	}
}
