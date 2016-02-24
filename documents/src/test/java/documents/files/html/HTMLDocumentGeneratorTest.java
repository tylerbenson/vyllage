package documents.files.html;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.dialect.SpringStandardDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import documents.model.DocumentHeader;
import documents.model.document.sections.SkillsSection;

public class HTMLDocumentGeneratorTest {

	private final String FORMATTED_NUMBER = "(123) 456-7899";

	private HTMLDocumentGenerator generator;

	private TemplateEngine templateEngine;

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

		generator = new HTMLDocumentGenerator(templateEngine);
	}

	@Test
	public void testFormat() {

		DocumentHeader resumeHeader = new DocumentHeader();

		resumeHeader.setPhoneNumber("1234567899");
		generator.format(resumeHeader);

		Assert.assertEquals(FORMATTED_NUMBER, resumeHeader.getPhoneNumber());
	}

	@Test
	public void testSortSections() {

		SkillsSection s1 = new SkillsSection();

		s1.setSectionPosition(2L);

		SkillsSection s2 = new SkillsSection();

		s2.setSectionPosition(1L);

		List<SkillsSection> skills = new ArrayList<>();

		skills.add(s1);
		skills.add(s2);

		List<SkillsSection> sortedSkills = skills.stream()
				.sorted(generator.sortSections()).collect(Collectors.toList());

		Assert.assertEquals(s1, sortedSkills.get(1));
		Assert.assertEquals(s2, sortedSkills.get(0));
	}

}
