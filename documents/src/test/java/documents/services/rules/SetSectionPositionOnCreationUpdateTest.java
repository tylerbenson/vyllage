package documents.services.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import documents.model.document.sections.CareerInterestsSection;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.SkillsSection;
import documents.model.document.sections.SummarySection;

public class SetSectionPositionOnCreationUpdateTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSectionIsNull() {
		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();
		List<DocumentSection> documentSections = new ArrayList<>(1);
		set.apply(null, documentSections);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSectionListIsNull() {
		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();
		SkillsSection skill = new SkillsSection();
		set.apply(skill, null);
	}

	@Test
	public void testApplyNoOtherSectionsExist() {

		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SkillsSection skill = new SkillsSection();

		List<DocumentSection> sections = new LinkedList<>();

		set.apply(skill, sections);

		long skillPosition = 1L;
		assertTrue(
				"Expected position " + skillPosition + ", got "
						+ skill.getSectionPosition(), skill
						.getSectionPosition().equals(skillPosition));
	}

	@Test
	public void testApplySectionIsSummary() {

		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SkillsSection skill = new SkillsSection();

		long skillPosition = 7L;
		skill.setSectionId(121L);
		skill.setSectionPosition(skillPosition);

		SummarySection summary = new SummarySection();

		long summaryPosition = 5L;
		summary.setSectionId(45L);
		summary.setSectionPosition(summaryPosition);

		CareerInterestsSection careerInterestsSection = new CareerInterestsSection();

		long careerInterestPosition = 6L;
		careerInterestsSection.setSectionId(210L);
		careerInterestsSection.setSectionPosition(careerInterestPosition);

		List<DocumentSection> sections = new LinkedList<>();
		sections.add(summary);
		sections.add(skill);
		sections.add(careerInterestsSection);

		set.apply(summary, sections);

		long first = 1;
		assertTrue("Expected summary to be in position " + first + ", got "
				+ summary.getSectionPosition(), summary.getSectionPosition()
				.equals(first));

		assertTrue("Expected position " + (first + 1) + ", got "
				+ careerInterestsSection.getSectionPosition(),
				careerInterestsSection.getSectionPosition().equals(first + 1));

		assertTrue(
				"Expected position " + (first + 2) + ", got "
						+ skill.getSectionPosition(), skill
						.getSectionPosition().equals(first + 2));
	}

	@Test
	public void testApplySectionNotPresent() {

		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SkillsSection skill = new SkillsSection();

		SummarySection summary = new SummarySection();

		long summaryPosition = 5L;
		summary.setSectionId(45L);
		summary.setSectionPosition(summaryPosition);

		CareerInterestsSection careerInterestsSection = new CareerInterestsSection();

		long careerInterestPosition = 6L;
		careerInterestsSection.setSectionId(210L);
		careerInterestsSection.setSectionPosition(careerInterestPosition);

		List<DocumentSection> sections = new LinkedList<>();
		sections.add(summary);
		sections.add(careerInterestsSection);

		set.apply(skill, sections);

		assertTrue("Expected position " + (summaryPosition + 1) + ", got "
				+ skill.getSectionPosition(), skill.getSectionPosition()
				.equals(summaryPosition + 1));

		assertTrue(
				"Expected position " + (careerInterestPosition + 1) + ", got "
						+ careerInterestsSection.getSectionPosition(),
				careerInterestsSection.getSectionPosition().equals(
						careerInterestPosition + 1));
	}

	@Test
	public void testApplySectionNotPresentSummaryNotPresent() {

		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SkillsSection skill = new SkillsSection();

		CareerInterestsSection careerInterestsSection = new CareerInterestsSection();

		long careerInterestPosition = 6L;
		careerInterestsSection.setSectionId(210L);
		careerInterestsSection.setSectionPosition(careerInterestPosition);

		List<DocumentSection> sections = new LinkedList<>();
		sections.add(careerInterestsSection);

		set.apply(skill, sections);

		long first = 1L;
		assertTrue(
				"Expected position " + first + ", got "
						+ skill.getSectionPosition(), skill
						.getSectionPosition().equals(first));

		assertTrue("Expected position " + (first + 1) + ", got "
				+ careerInterestsSection.getSectionPosition(),
				careerInterestsSection.getSectionPosition().equals(first + 1));
	}

	@Test
	public void testApplySectionNotPresentHasPositionAlreadySetIsOverwritten() {

		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SkillsSection skill = new SkillsSection();

		SummarySection summary = new SummarySection();
		long clientSetPosition = 26L;
		summary.setSectionPosition(clientSetPosition);

		long summaryPosition = 5L;
		summary.setSectionId(45L);
		summary.setSectionPosition(summaryPosition);

		CareerInterestsSection careerInterestsSection = new CareerInterestsSection();

		long careerInterestPosition = 6L;
		careerInterestsSection.setSectionId(210L);
		careerInterestsSection.setSectionPosition(careerInterestPosition);

		List<DocumentSection> sections = new LinkedList<>();
		sections.add(summary);
		sections.add(careerInterestsSection);

		set.apply(skill, sections);

		assertTrue("Expected position " + (summaryPosition + 1) + ", got "
				+ skill.getSectionPosition(), skill.getSectionPosition()
				.equals(summaryPosition + 1));

		assertTrue(
				"Expected position " + (careerInterestPosition + 1) + ", got "
						+ careerInterestsSection.getSectionPosition(),
				careerInterestsSection.getSectionPosition().equals(
						careerInterestPosition + 1));
		assertFalse("Expected to overwrite client set position.", skill
				.getSectionPosition().equals(clientSetPosition));
	}

	@Test
	public void testApplySectionPresent() {

		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SkillsSection skill = new SkillsSection();

		long skillPosition = 7L;
		skill.setSectionId(121L);
		skill.setSectionPosition(skillPosition);

		SummarySection summary = new SummarySection();

		long summaryPosition = 5L;
		summary.setSectionId(45L);
		summary.setSectionPosition(summaryPosition);

		CareerInterestsSection careerInterestsSection = new CareerInterestsSection();

		long careerInterestPosition = 6L;
		careerInterestsSection.setSectionId(210L);
		careerInterestsSection.setSectionPosition(careerInterestPosition);

		List<DocumentSection> sections = new LinkedList<>();
		sections.add(summary);
		sections.add(skill);
		sections.add(careerInterestsSection);

		set.apply(skill, sections);

		assertTrue(
				"Expected position " + skillPosition + ", got "
						+ skill.getSectionPosition(), skill
						.getSectionPosition().equals(skillPosition));
	}

	/**
	 * Test without the section to create / update.
	 */
	@Test
	public void testSortAndShiftByOneFrom() {
		SetSectionPositionOnCreationUpdate set = new SetSectionPositionOnCreationUpdate();

		SummarySection summary = new SummarySection();

		long summaryPosition = 5L;
		summary.setSectionPosition(summaryPosition);

		CareerInterestsSection careerInterestsSection = new CareerInterestsSection();

		long careerInterestPosition = 6L;
		careerInterestsSection.setSectionPosition(careerInterestPosition);

		SkillsSection skill = new SkillsSection();

		long skillPosition = 7L;
		skill.setSectionPosition(skillPosition);

		List<DocumentSection> sections = new LinkedList<>();
		sections.add(summary);
		sections.add(careerInterestsSection);
		sections.add(skill);

		long afterSummary = 2L;
		set.sortAndShiftByOneFrom(sections, afterSummary);

		assertTrue("Expected Summary Section to remain the same: "
				+ summaryPosition + ", got "
				+ sections.get(0).getSectionPosition(), sections.get(0)
				.getSectionPosition().equals(summaryPosition));

		assertTrue("Expected career position " + afterSummary + ", got "
				+ careerInterestsSection.getSectionPosition(),
				careerInterestsSection.getSectionPosition()
						.equals(afterSummary));

		assertTrue("Expected skill position " + (afterSummary + 1) + ", got "
				+ skill.getSectionPosition(), skill.getSectionPosition()
				.equals(afterSummary + 1));
	}

}
