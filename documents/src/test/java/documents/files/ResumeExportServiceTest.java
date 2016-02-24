package documents.files;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.dialect.SpringStandardDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.lowagie.text.DocumentException;

import documents.files.docx.DOCXDocumentGenerator;
import documents.files.html.HTMLDocumentGenerator;
import documents.files.pdf.PDFDocumentGenerator;
import documents.files.png.PNGDocumentGenerator;
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.SkillsSection;

public class ResumeExportServiceTest {

	// Unfortunately Thymeleaf TemplateEngine can't be mocked, the process
	// method is final.
	private TemplateEngine templateEngine = new TemplateEngine();

	private ResumeExportService service;

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

		service = new ResumeExportService(new HTMLDocumentGenerator(
				templateEngine), new PDFDocumentGenerator(),
				new PNGDocumentGenerator(), new DOCXDocumentGenerator());
	}

	@Test
	public void testGeneratePDFDocument() throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = "standard";

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
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePDFDocumentEmptyHeader() throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();

		String templateName = "standard";

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
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePDFDocumentEmptySections() throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = "standard";

		List<DocumentSection> sections = new ArrayList<>();

		ByteArrayOutputStream byteArrayOutputStream = service
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePDFDocumentEmptySectionsAndHeader()
			throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();

		String templateName = "standard";

		List<DocumentSection> sections = new ArrayList<>();

		ByteArrayOutputStream byteArrayOutputStream = service
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratePDFDocumentNullSectionsAndHeader()
			throws DocumentException {

		String templateName = "standard";

		DocumentHeader resumeHeader = null;
		List<DocumentSection> sections = null;
		ByteArrayOutputStream byteArrayOutputStream = service
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratePDFDocumentNullSections() throws DocumentException {

		String templateName = "standard";

		DocumentHeader resumeHeader = new DocumentHeader();
		List<DocumentSection> sections = null;
		ByteArrayOutputStream byteArrayOutputStream = service
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratePDFDocumentNullStyle() throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = null;

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
				.generatePDFDocument(resumeHeader, sections, templateName);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePNGDocument() throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = "standard";

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
				.generatePNGDocument(resumeHeader, sections, templateName, 64,
						98);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePNGDocumentWithZeroWidthHeight()
			throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = "standard";

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
				.generatePNGDocument(resumeHeader, sections, templateName, 0, 0);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePNGDocumentWithZeroWidth() throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = "standard";

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
				.generatePNGDocument(resumeHeader, sections, templateName, 0,
						98);

		assertNotNull(byteArrayOutputStream);

	}

	@Test
	public void testGeneratePNGDocumentWithZeroHeight()
			throws DocumentException {

		DocumentHeader resumeHeader = new DocumentHeader();
		resumeHeader.setPhoneNumber("1234567899");

		String templateName = "standard";

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
				.generatePNGDocument(resumeHeader, sections, templateName, 64,
						0);

		assertNotNull(byteArrayOutputStream);

	}
}
