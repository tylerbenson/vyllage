package documents.model.document.sections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		AchievementsSection ds1 = new AchievementsSection();
		ds1.setTitle("a");

		AchievementsSection ds2 = new AchievementsSection();
		ds2.setTitle("a");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsProjectsSection() {
		ProjectsSection ds1 = new ProjectsSection();
		ds1.setTitle("b");

		ProjectsSection ds2 = new ProjectsSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsNullFalse() {
		ProjectsSection ds1 = new ProjectsSection();

		assertFalse(ds1.equals(null));
	}

	@Test
	public void testEqualsCareerInterestsSection() {
		CareerInterestsSection ds1 = new CareerInterestsSection();
		ds1.setTitle("b");

		CareerInterestsSection ds2 = new CareerInterestsSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsEducationSection() {
		EducationSection ds1 = new EducationSection();
		ds1.setTitle("b");

		EducationSection ds2 = new EducationSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsJobExperienceSection() {
		JobExperienceSection ds1 = new JobExperienceSection();
		ds1.setTitle("b");

		JobExperienceSection ds2 = new JobExperienceSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsPersonalReferencesSection() {
		PersonalReferencesSection ds1 = new PersonalReferencesSection();
		ds1.setTitle("b");

		PersonalReferencesSection ds2 = new PersonalReferencesSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsProfessionalReferencesSection() {
		ProfessionalReferencesSection ds1 = new ProfessionalReferencesSection();
		ds1.setTitle("b");

		ProfessionalReferencesSection ds2 = new ProfessionalReferencesSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsSkillsSection() {
		SkillsSection ds1 = new SkillsSection();
		ds1.setTitle("b");

		SkillsSection ds2 = new SkillsSection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

	@Test
	public void testEqualsSummarySection() {
		SummarySection ds1 = new SummarySection();
		ds1.setTitle("b");

		SummarySection ds2 = new SummarySection();
		ds2.setTitle("b");

		assertTrue(ds1.equals(ds2));
	}

}
