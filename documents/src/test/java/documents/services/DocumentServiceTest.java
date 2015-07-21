package documents.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.constants.DocumentAccessEnum;
import documents.model.constants.DocumentTypeEnum;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.repository.CommentRepository;
import documents.repository.DocumentAccessRepository;
import documents.repository.DocumentRepository;
import documents.repository.DocumentSectionRepository;
import documents.repository.ElementNotFoundException;
import documents.repository.IRepository;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

	private static final String JSON = "{" + "\"type\": \"organization\","
			+ "\"title\": \"experience\"," + "\"sectionId\": 124,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"Sep 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\": true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\": \"I was in charge of...\"" + "}";

	@InjectMocks
	private DocumentService service;

	@Mock
	private DocumentSectionRepository dsRepository;

	@Mock
	private IRepository<Document> dRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private AccountService accountService;

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private DocumentAccessRepository documentAccessRepository;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadSingleDocumentSectionTest() throws ElementNotFoundException {
		long sectionId = 1;

		String json = JSON;

		Mockito.doReturn(EducationSection.fromJSON(json))
				.when(dsRepository).get(sectionId);

		DocumentSection documentSection = service.getDocumentSection(sectionId);

		Assert.assertNotNull(documentSection);
	}

	@Test
	public void loadSeveralDocumentSectionsTest()
			throws ElementNotFoundException {
		long documentId = 1;

		String json1 = JSON;

		String json2 = JSON;

		Mockito.doReturn(
				Arrays.asList(EducationSection.fromJSON(json1),
						EducationSection.fromJSON(json2)))
				.when(dsRepository).getDocumentSections(documentId);

		List<DocumentSection> documentSection = service
				.getDocumentSections(documentId);

		Assert.assertNotNull(documentSection);
	}

	@Test
	public void getCommentsForSectionSuccessfullTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Long sectionId = 124L;
		Long userId = 0L;

		Mockito.when(commentRepository.getCommentsForSection(sectionId))
				.thenReturn(comments(sectionId));

		Mockito.when(
				accountService.getNamesForUsers(Arrays.asList(userId), request))
				.thenReturn(accountNames(userId));

		List<Comment> commentsForSection = service.getCommentsForSection(
				request, sectionId);

		Assert.assertNotNull(commentsForSection);
		Assert.assertFalse(commentsForSection.isEmpty());
		Assert.assertEquals(sectionId, commentsForSection.get(0).getSectionId());

	}

	@Test
	public void getCommentsForSectionNullComments() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Long sectionId = 124L;

		Mockito.when(commentRepository.getCommentsForSection(sectionId))
				.thenReturn(null);

		service.getCommentsForSection(request, sectionId);
	}

	@Test
	public void getCommentsForSectionEmptyComments() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Long sectionId = 124L;

		Mockito.when(commentRepository.getCommentsForSection(sectionId))
				.thenReturn(new ArrayList<Comment>());

		service.getCommentsForSection(request, sectionId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getDocumentByUserDocumentNotFoundCreatesOne()
			throws ElementNotFoundException {
		Long userId = 99999999999L;

		Mockito.when(documentRepository.getDocumentByUser(userId)).thenThrow(
				ElementNotFoundException.class);

		Mockito.when(documentRepository.save(Mockito.any(Document.class)))
				.thenReturn(document(userId));

		Document documentByUser = service.getDocumentByUser(userId);

		Assert.assertNotNull(documentByUser);
		Assert.assertNotNull(documentByUser.getDocumentId());
		Assert.assertNotNull(documentByUser.getDateCreated());
		Assert.assertNotNull(documentByUser.getLastModified());
		Assert.assertEquals(userId, documentByUser.getUserId());

	}

	@Test
	public void getDocumentByUserFound() throws ElementNotFoundException {
		Long userId = 0L;

		Mockito.when(documentRepository.getDocumentByUser(userId)).thenReturn(
				document(userId));

		Document documentByUser = service.getDocumentByUser(userId);

		Assert.assertNotNull(documentByUser);
		Assert.assertNotNull(documentByUser.getDocumentId());
		Assert.assertNotNull(documentByUser.getDateCreated());
		Assert.assertNotNull(documentByUser.getLastModified());
		Assert.assertEquals(userId, documentByUser.getUserId());

	}

	@Test
	public void getRecentUsersForDocument() {
		Long documentId = 0L;
		List<Long> list = Arrays.asList(0L, 2L);
		Mockito.when(documentRepository.getRecentUsersForDocument(documentId))
				.thenReturn(list);

		boolean containsAll = service.getRecentUsersForDocument(documentId)
				.containsAll(list);

		Assert.assertTrue(containsAll);
	}

	@Test
	public void deleteDocumentMethodIsCalled() {
		Long userId = 0L;

		service.deleteDocumentsFromUser(userId);

		Mockito.verify(documentRepository).deleteForUser(userId);

	}

	@Test
	public void checkAccessWriteOk() {
		Long userId = 3L;
		Long documentId = 42L;

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.WRITE);

		Mockito.when(documentAccessRepository.get(userId, documentId))
				.thenReturn(Optional.ofNullable(documentAccess));

		Assert.assertTrue(service.checkAccess(userId, documentId,
				DocumentAccessEnum.WRITE));

	}

	@Test
	public void checkAccessWriteFalse() {
		Long userId = 3L;
		Long documentId = 42L;

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);

		Mockito.when(documentAccessRepository.get(userId, documentId))
				.thenReturn(Optional.ofNullable(documentAccess));

		Assert.assertFalse(service.checkAccess(userId, documentId,
				DocumentAccessEnum.WRITE));

	}

	@Test
	public void checkAccessReadOk() {
		Long userId = 3L;
		Long documentId = 42L;

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.READ);

		Mockito.when(documentAccessRepository.get(userId, documentId))
				.thenReturn(Optional.ofNullable(documentAccess));

		Assert.assertTrue(service.checkAccess(userId, documentId,
				DocumentAccessEnum.READ));

	}

	@Test
	public void checkAccessReadWhenAccessIsWriteOK() {
		Long userId = 3L;
		Long documentId = 42L;

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setAccess(DocumentAccessEnum.WRITE);

		Mockito.when(documentAccessRepository.get(userId, documentId))
				.thenReturn(Optional.ofNullable(documentAccess));

		Assert.assertTrue(service.checkAccess(userId, documentId,
				DocumentAccessEnum.READ));

	}

	@Test
	public void checkAccessReadWhenAccessIsNull() {
		Long userId = 3L;
		Long documentId = 42L;

		Mockito.when(documentAccessRepository.get(userId, documentId))
				.thenReturn(Optional.ofNullable(null));

		Assert.assertFalse(service.checkAccess(userId, documentId,
				DocumentAccessEnum.READ));

	}

	@Test
	public void checkAccessWriteWhenAccessIsNull() {
		Long userId = 3L;
		Long documentId = 42L;

		Mockito.when(documentAccessRepository.get(userId, documentId))
				.thenReturn(Optional.ofNullable(null));

		Assert.assertFalse(service.checkAccess(userId, documentId,
				DocumentAccessEnum.WRITE));

	}

	public void getDocumentsByType() {
		List<Document> documentByUserAndType = service
				.getDocumentByUserAndType(0L, DocumentTypeEnum.RESUME);

		Assert.assertNotNull(documentByUserAndType);
		Assert.assertFalse(documentByUserAndType.isEmpty());
		Assert.assertTrue(documentByUserAndType.get(0).getDocumentType()
				.equals(DocumentTypeEnum.RESUME.name()));
	}

	private Document document(Long userId) {
		Document document = new Document();
		document.setUserId(userId);
		document.setDocumentId(1L);
		document.setLastModified(LocalDateTime.now());
		document.setDateCreated(LocalDateTime.now());
		document.setDocumentType(DocumentTypeEnum.RESUME.name());
		return document;
	}

	private List<Comment> comments(Long sectionId) {
		Comment comment = new Comment();
		comment.setUserId(0L);
		comment.setSectionId(sectionId);

		return Arrays.asList(comment);
	}

	private List<AccountNames> accountNames(Long userId) {
		AccountNames ac = new AccountNames();
		ac.setUserId(userId);
		ac.setFirstName("Juan");
		ac.setLastName("Perez");

		return Arrays.asList(ac);
	}

}
