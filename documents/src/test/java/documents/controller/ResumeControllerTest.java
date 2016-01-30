package documents.controller;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import documents.model.Document;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.RezcoreService;

@RunWith(MockitoJUnitRunner.class)
public class ResumeControllerTest {

	private static final String SECTION_124 = "{"
			+ "\"type\": \"JobExperienceSection\","
			+ "\"title\": \"experience\"," + "\"sectionId\": 124,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"Sep 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\": true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\":[\"I was in charge of...\"" + "]}";

	private static final String SECTION_123 = "{"
			+ "\"type\": \"SummarySection\"," + "\"title\": \"career goal\","
			+ "\"sectionId\": 123," + "\"sectionPosition\": 1,"
			+ "\"state\": \"shown\","
			+ "\"description\": \"this is my goal statement.\"" + "}";

	@SuppressWarnings("unused")
	private static final String HEADER = "{"
			+ "\"firstName\": \"Nathan\","
			+ "\"middleName\": \"M\","
			+ "\"lastName\": \"Benson\","
			+ "\"tagline\": \"Technology Enthusiast analyzing, building, and expanding solutions\""
			+ "}";

	private ResumeController controller;

	private DocumentService documentService = Mockito
			.mock(DocumentService.class);

	private AccountService accountService = Mockito.mock(AccountService.class);

	private RezcoreService rezcoreService = Mockito.mock(RezcoreService.class);

	@Before
	public void setUp() {
		controller = new ResumeController(documentService, accountService,
				rezcoreService);
	}

	// resume/0/section/124
	@Test
	public void getSectionFromResumeTest() throws ElementNotFoundException {

		Mockito.when(documentService.getDocumentSection(124L)).thenReturn(
				DocumentSection.fromJSON(SECTION_124));

		given().standaloneSetup(controller).when().get("/resume/0/section/124")
				.then().statusCode(200).body("sectionId", equalTo(124));

		// get("/resume/0/section").then().body("[0].sectionId", equalTo(124));

	}

	// resume/0/section
	@Test
	public void getAllSectionsFromResumeTest() throws ElementNotFoundException {

		long documentId = 0;

		Mockito.when(documentService.getDocumentSections(documentId))
				.thenReturn(
						Arrays.asList(DocumentSection.fromJSON(SECTION_123),
								EducationSection.fromJSON(SECTION_124)));

		given().standaloneSetup(controller).when()
				.get("/resume/" + documentId + "/section/").then()
				.statusCode(200).body("[0].sectionId", equalTo(123))
				.body("[1].sectionId", equalTo(124));

		// get("/resume/0/section").then().body("[0].sectionId", equalTo(123));

	}

	// resume/0/section post
	public void saveDocumentSection() throws ElementNotFoundException {
		long documentId = 0;
		Document document = Mockito.mock(Document.class);

		Mockito.when(documentService.getDocument(documentId)).thenReturn(
				document);

		Mockito.when(documentService.getDocumentSection(124L)).thenReturn(
				EducationSection.fromJSON(SECTION_124));

		given().standaloneSetup(controller).when()
				.post("/resume/" + documentId + "/section/").then()
				.statusCode(200).body("sectionId", equalTo(124));
	}

	// resume/0/section/1000
	@SuppressWarnings("unchecked")
	@Test
	public void getSectionFromResumeNotFoundTest()
			throws ElementNotFoundException {

		long documentSectionId = -1;

		Mockito.when(documentService.getDocumentSection(documentSectionId))
				.thenThrow(ElementNotFoundException.class);

		given().standaloneSetup(controller).when()
				.get("/resume/0/section/" + documentSectionId).then()
				.assertThat().statusCode(404);

	}

}
