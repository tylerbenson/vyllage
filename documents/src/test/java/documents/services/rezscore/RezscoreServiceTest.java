package documents.services.rezscore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

import documents.ApplicationTestConfig;
import documents.model.DocumentHeader;
import documents.model.document.sections.SkillsSection;

// @RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RezscoreServiceTest {

	private RezscoreService rezscoreService;

	private RestTemplate restTemplate = new RestTemplate();

	@Before
	public void setUp() {

		rezscoreService = new RezscoreService(restTemplate);
	}

	@Test
	public void test() {
		DocumentHeader dh = new DocumentHeader();

		dh.setAddress("address");
		dh.setEmail("email@email.com");
		dh.setFirstName("Name");
		dh.setLastName("last");

		SkillsSection ds = new SkillsSection();
		ds.setDocumentId(42L);
		ds.setTags(Lists.newArrayList("one", "two", "Java"));

		Optional<RezscoreResult> analysis = rezscoreService.getRezcoreAnalysis(
				dh, Arrays.asList(ds));

		assertNotNull(analysis.get());

		assertNotNull(analysis.get().getResume());

		assertNotNull(analysis.get().getRezscore());
	}

	@Test
	public void testNullHeader() {
		DocumentHeader dh = null;

		SkillsSection ds = new SkillsSection();
		ds.setDocumentId(42L);
		ds.setTags(Lists.newArrayList("one", "two", "Java"));

		Optional<RezscoreResult> analysis = rezscoreService.getRezcoreAnalysis(
				dh, Arrays.asList(ds));

		assertTrue(!analysis.isPresent());

	}

	@Test
	public void testNullDocumentSections() {
		DocumentHeader dh = new DocumentHeader();

		dh.setAddress("address");
		dh.setEmail("email@email.com");
		dh.setFirstName("Name");
		dh.setLastName("last");

		Optional<RezscoreResult> analysis = rezscoreService.getRezcoreAnalysis(
				dh, null);

		assertTrue(!analysis.isPresent());

	}

	@Test
	public void testEmptyDocumentSections() {
		DocumentHeader dh = new DocumentHeader();

		dh.setAddress("address");
		dh.setEmail("email@email.com");
		dh.setFirstName("Name");
		dh.setLastName("last");

		Optional<RezscoreResult> analysis = rezscoreService.getRezcoreAnalysis(
				dh, new ArrayList<>());

		assertTrue(!analysis.isPresent());

	}

}
