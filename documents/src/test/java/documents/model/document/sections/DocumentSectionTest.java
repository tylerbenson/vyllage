package documents.model.document.sections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import documents.model.constants.SectionType;

public class DocumentSectionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testSetType() {
		DocumentSection ds = new AchievementsSection();
		ds.setType("asdasd");
	}

	@Test
	public void testfromJSONFails() {

		assertTrue(DocumentSection.fromJSON("{ ") == null);
	}

	@Test
	public void testEqualsAchievementsSection() {
		ProjectsSection ds1 = new ProjectsSection();
		ds1.setType(SectionType.PROJECTS_SECTION.type());

		ProjectsSection ds2 = new ProjectsSection();
		ds2.setType(SectionType.PROJECTS_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsNullFalse() {
		ProjectsSection ds1 = new ProjectsSection();
		ds1.setType(SectionType.PROJECTS_SECTION.type());

		assertFalse(ds1.equals(null));
	}

	@Test
	public void testEqualsCareerInterestsSection() {
		CareerInterestsSection ds1 = new CareerInterestsSection();
		ds1.setType(SectionType.CAREER_INTERESTS_SECTION.type());

		CareerInterestsSection ds2 = new CareerInterestsSection();
		ds2.setType(SectionType.CAREER_INTERESTS_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsEducationSection() {
		EducationSection ds1 = new EducationSection();
		ds1.setType(SectionType.EDUCATION_SECTION.type());

		EducationSection ds2 = new EducationSection();
		ds2.setType(SectionType.EDUCATION_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsPersonalReferencesSection() {
		PersonalReferencesSection ds1 = new PersonalReferencesSection();
		ds1.setType(SectionType.PERSONAL_REFERENCES_SECTION.type());

		PersonalReferencesSection ds2 = new PersonalReferencesSection();
		ds2.setType(SectionType.PERSONAL_REFERENCES_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsProfessionalReferencesSection() {
		ProfessionalReferencesSection ds1 = new ProfessionalReferencesSection();
		ds1.setType(SectionType.PROFESSIONAL_REFERENCES_SECTION.type());

		ProfessionalReferencesSection ds2 = new ProfessionalReferencesSection();
		ds2.setType(SectionType.PROFESSIONAL_REFERENCES_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsSkillsSection() {
		SkillsSection ds1 = new SkillsSection();
		ds1.setType(SectionType.SKILLS_SECTION.type());

		SkillsSection ds2 = new SkillsSection();
		ds2.setType(SectionType.SKILLS_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsSummarySection() {
		SummarySection ds1 = new SummarySection();
		ds1.setType(SectionType.SUMMARY_SECTION.type());

		SummarySection ds2 = new SummarySection();
		ds2.setType(SectionType.SUMMARY_SECTION.type());

		assertTrue(ds1.equals(ds2));
	}
}
