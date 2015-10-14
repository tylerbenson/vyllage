package documents.services.rules;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import user.common.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import documents.ApplicationTestConfig;
import documents.model.constants.SectionType;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.SkillsSection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MergeOnSectionDuplicateIntegTest {

	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext wContext;

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
	}

	@Test
	public void testMergeSections() throws JsonProcessingException, Exception {
		generateAndLoginUser();
		final Long documentId = 0L;

		final String tag1 = "skill1";
		final String tag2 = "skill2";

		// save first Skill section.

		SkillsSection skill1 = new SkillsSection();
		skill1.setDocumentId(documentId);
		skill1.setTags(Arrays.asList(tag1));

		MvcResult section1Result = mockMvc
				.perform(
						post("/resume/" + documentId + "/section/")
								.contentType(
										ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(skill1)))
				.andExpect(status().isOk()).andReturn();

		assertNotNull("Expected saved section1, got null. ", section1Result);

		SkillsSection savedSkill1 = mapper.readValue(section1Result
				.getResponse().getContentAsString(), SkillsSection.class);

		assertTrue("Tag1 not found.", savedSkill1.getTags().contains(tag1));

		assertNotNull("Skill1 sectionId is null.", savedSkill1.getSectionId());

		// save second Skill section.

		SkillsSection skill2 = new SkillsSection();
		skill2.setSectionId(52L);
		skill2.setDocumentId(documentId);
		skill2.setTags(Arrays.asList(tag2));

		MvcResult section2Result = mockMvc
				.perform(
						post("/resume/" + documentId + "/section/")
								.contentType(
										ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(skill2)))
				.andExpect(status().isOk()).andReturn();

		assertNotNull("Expected saved section2, got null. ", section2Result);

		SkillsSection savedSkill2 = mapper.readValue(section2Result
				.getResponse().getContentAsString(), SkillsSection.class);

		assertNotNull("Skill2 sectionId is null.", savedSkill2.getSectionId());

		assertTrue("Tag1 and Tag2 not found.", savedSkill2.getTags()
				.containsAll(Arrays.asList(tag1, tag2)));

		// retrieve all sections.

		MvcResult allSections = mockMvc
				.perform(get("/resume/" + documentId + "/section/"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull("No sections returned.", allSections);

		DocumentSection[] sections = mapper.readValue(allSections.getResponse()
				.getContentAsString(), DocumentSection[].class);

		// assert we have only one skill section.
		assertTrue(
				"Found more than one Skill Section, sections not merged.",
				Arrays.asList(sections)
						.stream()
						.filter(ds -> SectionType.SKILLS_SECTION.type().equals(
								ds.getType())).collect(Collectors.toList())
						.size() == 1);

		SkillsSection existingSkillSection = (SkillsSection) Arrays
				.asList(sections)
				.stream()
				.filter(ds -> SectionType.SKILLS_SECTION.type().equals(
						ds.getType())).collect(Collectors.toList()).get(0);

		// it contains our tags

		assertTrue(
				"Retrieved all sections, checked skills and Tag1, Tag2 not found.",
				existingSkillSection.getTags().containsAll(
						Arrays.asList(tag1, tag2)));

		Long existingSkillSectionId = 128L;
		assertTrue(
				"Expected to find Skill section with id '128' from scripts. Got "
						+ existingSkillSection.getSectionId(),
				existingSkillSectionId.equals(existingSkillSection
						.getSectionId()));

	}

	@Test
	public void testMergeSectionTheresOnlyOne() throws JsonProcessingException,
			Exception {
		generateAndLoginUser();
		final Long documentId = 0L;
		Long existingSkillSectionId = 128L;

		final String tag1 = "skill1";

		// save first Skill section.

		SkillsSection skill1 = new SkillsSection();
		skill1.setSectionId(540L);
		skill1.setDocumentId(documentId);
		skill1.setTags(Arrays.asList(tag1));

		MvcResult section1Result = mockMvc
				.perform(
						post("/resume/" + documentId + "/section/")
								.contentType(
										ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(skill1)))
				.andExpect(status().isOk()).andReturn();

		assertNotNull("Expected saved section1, got null. ", section1Result);

		SkillsSection savedSkill1 = mapper.readValue(section1Result
				.getResponse().getContentAsString(), SkillsSection.class);

		assertTrue("Tag1 not found.", savedSkill1.getTags().contains(tag1));

		assertNotNull("Skill1 sectionId is null.", savedSkill1.getSectionId());
		assertNotNull(
				"Skill1 sectionId is not the same as the existing sectionId. Expected "
						+ existingSkillSectionId + " got "
						+ savedSkill1.getSectionId(),
				existingSkillSectionId.equals(savedSkill1.getSectionId()));

		// retrieve all sections.

		MvcResult allSections = mockMvc
				.perform(get("/resume/" + documentId + "/section/"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull("No sections returned.", allSections);

		DocumentSection[] sections = mapper.readValue(allSections.getResponse()
				.getContentAsString(), DocumentSection[].class);

		// assert we have only one skill section.
		assertTrue(
				"Found more than one Skill Section, sections not merged.",
				Arrays.asList(sections)
						.stream()
						.filter(ds -> SectionType.SKILLS_SECTION.type().equals(
								ds.getType())).collect(Collectors.toList())
						.size() == 1);

		SkillsSection existingSkillSection = (SkillsSection) Arrays
				.asList(sections)
				.stream()
				.filter(ds -> SectionType.SKILLS_SECTION.type().equals(
						ds.getType())).collect(Collectors.toList()).get(0);

		// it contains our tags

		assertTrue(
				"Retrieved all sections, checked skills and Tag1, Tag2 not found.",
				existingSkillSection.getTags().containsAll(Arrays.asList(tag1)));

		assertTrue(
				"Expected to find Skill section with id '128' from scripts. Got "
						+ existingSkillSection.getSectionId(),
				existingSkillSectionId.equals(existingSkillSection
						.getSectionId()));

	}

	private User generateAndLoginUser() {
		User o = mock(User.class);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(o.getUserId()).thenReturn(0L);
		return o;
	}
}
