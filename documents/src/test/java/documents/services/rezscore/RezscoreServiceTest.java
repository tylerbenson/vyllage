package documents.services.rezscore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
import documents.services.rezscore.result.RezscoreResult;

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

		SkillsSection ds1 = new SkillsSection();
		ds1.setDocumentId(42L);
		ds1.setTags(Lists.newArrayList("one", "two", "Java"));
		ds1.setSectionPosition(1L);

		SkillsSection ds2 = new SkillsSection();
		ds2.setDocumentId(42L);
		ds2.setTags(Lists.newArrayList("test1", "test2", "test3"));
		ds2.setSectionPosition(2L);

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(dh, Arrays.asList(ds1, ds2));

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

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(dh, Arrays.asList(ds));

		assertFalse(analysis.isPresent());

	}

	@Test
	public void testNullDocumentSections() {
		DocumentHeader dh = new DocumentHeader();

		dh.setAddress("address");
		dh.setEmail("email@email.com");
		dh.setFirstName("Name");
		dh.setLastName("last");

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(dh, null);

		assertFalse(analysis.isPresent());

	}

	@Test
	public void testEmptyDocumentSections() {
		DocumentHeader dh = new DocumentHeader();

		dh.setAddress("address");
		dh.setEmail("email@email.com");
		dh.setFirstName("Name");
		dh.setLastName("last");

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(dh, new ArrayList<>());

		assertFalse(analysis.isPresent());

	}

}
