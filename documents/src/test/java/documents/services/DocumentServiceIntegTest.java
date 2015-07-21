package documents.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;

import com.fasterxml.jackson.core.JsonProcessingException;

import documents.Application;
import documents.model.AccountContact;
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.repository.CommentRepository;
import documents.repository.DocumentAccessRepository;
import documents.repository.DocumentRepository;
import documents.repository.DocumentSectionRepository;
import documents.repository.ElementNotFoundException;
import documents.repository.SuggestionRepository;
import documents.utilities.OrderSectionValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentServiceIntegTest {

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentSectionRepository documentSectionRepository;

	@Inject
	private CommentRepository commentRepository;

	@Inject
	private SuggestionRepository suggestionRepository;

	@Inject
	private AccountService accountService;

	@Inject
	private OrderSectionValidator orderSectionValidator;

	@Inject
	private DocumentAccessRepository documentAccessRepository;

	private DocumentService service;
	
	@Inject
	private DocumentSectionDataMigration migration;

	@Before
	public void setUp() throws Exception {
		migration.migrate();
	}

	@Before
	public void setUp() {
		service = new DocumentService(documentRepository,
				documentSectionRepository, commentRepository,
				suggestionRepository, accountService, orderSectionValidator,
				documentAccessRepository);
	}

	@Test
	public void orderSectionsOK() throws JsonProcessingException,
			ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId,
				Arrays.asList(123L, 125L, 126L, 124L));

		List<DocumentSection> resumeSections = service
				.getDocumentSections(documentId);

		Assert.assertNotNull(resumeSections);
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(123L)
						&& rs.getSectionPosition().equals(1L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(125L)
						&& rs.getSectionPosition().equals(2L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(126L)
						&& rs.getSectionPosition().equals(3L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(124L)
						&& rs.getSectionPosition().equals(4L)));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSections()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId, Arrays.asList(123L));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsNumberOfSectionsCorrectIdsDoNotMatch()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId,
				Arrays.asList(123L, 200L, 201L, 245L));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSectionsNull()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		List<Long> documentSectionIds = null;
		service.orderDocumentSections(documentId, documentSectionIds);

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSectionsEmpty()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId, Arrays.asList());

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsDuplicateSectionIds()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId, Arrays.asList(123L, 123L));

	}

	@Test(expected = ElementNotFoundException.class)
	public void orderSectionsDocumentDoesNotExist()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 9999999999L;
		service.orderDocumentSections(documentId,
				Arrays.asList(123L, 124L, 125L, 126L));

	}

	@Test
	public void getResumeHeaderOk() throws ElementNotFoundException,
			JsonProcessingException, IOException {
		User user = generateAndLoginUser();

		Long userId = 0L;
		Long documentId = 0L;

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Document document = Mockito.mock(Document.class);

		AccountService accountService = Mockito.mock(AccountService.class);

		Mockito.when(document.getUserId()).thenReturn(userId);
		Mockito.when(
				accountService.getContactDataForUsers(request,
						Arrays.asList(userId))).thenReturn(
				accountContact(userId));
		Mockito.when(user.getUserId()).thenReturn(userId);

		DocumentService service = new DocumentService(documentRepository,
				documentSectionRepository, commentRepository,
				suggestionRepository, accountService, orderSectionValidator,
				documentAccessRepository);

		DocumentHeader resumeHeader = service.getDocumentHeader(request,
				documentId, user);

		Assert.assertNotNull(resumeHeader);
	}

	private User generateAndLoginUser() {
		User o = Mockito.mock(User.class);

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(
				authentication);
		SecurityContextHolder.setContext(securityContext);
		return o;
	}

	private List<AccountContact> accountContact(Long userId) {
		AccountContact ac = new AccountContact();
		ac.setFirstName("first");
		ac.setEmail("email");
		ac.setUserId(userId);
		ac.setLastName("last");

		return Arrays.asList(ac);
	}
}
