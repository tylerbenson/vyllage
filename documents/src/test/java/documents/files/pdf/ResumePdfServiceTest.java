package documents.files.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import documents.model.DocumentHeader;
import documents.model.document.sections.SkillsSection;

public class ResumePdfServiceTest {

	private final String FORMATTED_NUMBER = "(123) 456-7899";

	// later
	// @Test
	// public void testGeneratePdfDocument() {
	//
	// }

	@Test
	public void testFormat() {
		ResumeExportService service = new ResumeExportService();

		DocumentHeader resumeHeader = new DocumentHeader();

		resumeHeader.setPhoneNumber("1234567899");
		service.format(resumeHeader);

		Assert.assertEquals(FORMATTED_NUMBER, resumeHeader.getPhoneNumber());
	}

	@Test
	public void testSortSections() {
		ResumeExportService service = new ResumeExportService();

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
