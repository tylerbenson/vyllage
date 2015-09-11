package documents.files.pdf;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.dialect.SpringStandardDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.lowagie.text.DocumentException;

import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.SkillsSection;

public class ResumeExportServiceTest {

	private final String FORMATTED_NUMBER = "(123) 456-7899";

	// Unfortunately Thymeleaf TemplateEngine can't be mocked, the process
	// method is final.
	private TemplateEngine templateEngine = new TemplateEngine();

	@Before
	public void setUp() {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheable(false);
		templateResolver.setCacheTTLMs(3600000L);
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new LayoutDialect());
		templateEngine.addDialect(new SpringStandardDialect());
	}

	@Test
	public void testGeneratePDFDocument() throws DocumentException {
		ResumeExportService service = new ResumeExportService(templateEngine);

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String styleName = "default";

		List<DocumentSection> sections = new ArrayList<>();

		SkillsSection s1 = new SkillsSection();
		s1.setDocumentId(0L);
		s1.setLastModified(LocalDateTime.now());
		s1.setNumberOfComments(1);
		s1.setSectionId(1L);
		s1.setSectionVersion(1L);
		s1.setTitle("Skills");
		s1.setType("SkillsSection");
		s1.setTags(Arrays.asList("Skill1", "Skill2"));

		sections.add(s1);

		ByteArrayOutputStream byteArrayOutputStream = service
				.generatePDFDocument(resumeHeader, sections, styleName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePNGDocument() throws DocumentException {
		ResumeExportService service = new ResumeExportService(templateEngine);

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String styleName = "default";

		List<DocumentSection> sections = new ArrayList<>();

		SkillsSection s1 = new SkillsSection();
		s1.setDocumentId(0L);
		s1.setLastModified(LocalDateTime.now());
		s1.setNumberOfComments(1);
		s1.setSectionId(1L);
		s1.setSectionVersion(1L);
		s1.setTitle("Skills");
		s1.setType("SkillsSection");
		s1.setTags(Arrays.asList("Skill1", "Skill2"));

		sections.add(s1);

		ByteArrayOutputStream byteArrayOutputStream = service
				.generatePNGDocument(resumeHeader, sections, styleName, 64, 98);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testFormat() {

		ResumeExportService service = new ResumeExportService(templateEngine);

		DocumentHeader resumeHeader = new DocumentHeader();

		resumeHeader.setPhoneNumber("1234567899");
		service.format(resumeHeader);

		Assert.assertEquals(FORMATTED_NUMBER, resumeHeader.getPhoneNumber());
	}

	@Test
	public void testSortSections() {
		ResumeExportService service = new ResumeExportService(templateEngine);

		SkillsSection s1 = new SkillsSection();

		s1.setSectionPosition(2L);

		SkillsSection s2 = new SkillsSection();

		s2.setSectionPosition(1L);

		List<SkillsSection> skills = new ArrayList<>();

		skills.add(s1);
		skills.add(s2);

		List<SkillsSection> sortedSkills = skills.stream()
				.sorted(service.sortSections()).collect(Collectors.toList());

		Assert.assertEquals(s1, sortedSkills.get(1));
		Assert.assertEquals(s2, sortedSkills.get(0));
	}

}
