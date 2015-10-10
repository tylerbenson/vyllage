package documents.services.rules;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import documents.model.document.sections.CareerInterestsSection;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.SkillsSection;

public class MergeOnSectionDuplicateTest {

	@Test
	public void testApply() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		merger.apply(section, documentSections);

		assertTrue("Expected having to delete sections.",
				merger.haveToDeleteSections());

		assertTrue(
				"Expected: " + other + " got " + merger.getSectionsToDelete(),
				merger.getSectionsToDelete().contains(other));

	}

	@Test
	public void testApplyEmptyCollection() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));

		List<DocumentSection> documentSections = Collections.emptyList();

		merger.apply(section, documentSections);

		assertFalse("Expected not having to delete sections.",
				merger.haveToDeleteSections());

		assertTrue(
				"Expected: " + Collections.emptyList() + " got "
						+ merger.getSectionsToDelete(), merger
						.getSectionsToDelete().equals(Collections.emptyList()));

	}

	@Test
	public void testApplySectionNotPresent() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));

		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Collections.emptyList();

		merger.apply(section, documentSections);

		assertFalse("Expected not having to delete sections.",
				merger.haveToDeleteSections());

		assertTrue(
				"Expected: " + Collections.emptyList() + " got "
						+ merger.getSectionsToDelete(), merger
						.getSectionsToDelete().equals(Collections.emptyList()));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplySectionNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = null;
		SkillsSection other = new SkillsSection();

		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		merger.apply(section, documentSections);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyCollectionNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		List<DocumentSection> documentSections = null;

		merger.apply(section, documentSections);

	}

	@Test
	public void testIsSectionPresentTrue() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		assertTrue("Expected to find another section of the same type.",
				merger.isSectionPresent(section, documentSections));

	}

	@Test
	public void testIsSectionPresentFalse() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		assertFalse(
				"Did not expecte to find another section of the same type.",
				merger.isSectionPresent(section, documentSections));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSectionPresentSectionIsNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = null;
		CareerInterestsSection other = new CareerInterestsSection();

		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		merger.isSectionPresent(section, documentSections);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSectionPresentCollectionIsNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		List<DocumentSection> documentSections = null;

		merger.isSectionPresent(section, documentSections);
	}

	@Test
	public void testAddSectionsToDelete() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		merger.addSectionsToDelete(section, documentSections);

		assertTrue("Expected having to delete sections.",
				merger.haveToDeleteSections());

		assertTrue(
				"Expected: " + other + " got " + merger.getSectionsToDelete(),
				merger.getSectionsToDelete().contains(other));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSectionsToDeleteSectionIsNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = null;
		SkillsSection other = new SkillsSection();

		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		merger.addSectionsToDelete(section, documentSections);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSectionsToDeleteCollectionIsNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));

		List<DocumentSection> documentSections = null;

		merger.addSectionsToDelete(section, documentSections);

	}

	@Test
	public void testAddSectionsToDeleteCollectionIsEmptyExpectNotHavingToDeleteAndEmptyResult() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));

		List<DocumentSection> documentSections = Collections.emptyList();

		merger.addSectionsToDelete(section, documentSections);

		assertFalse("Expected not having to delete sections.",
				merger.haveToDeleteSections());

		assertTrue(
				"Expected: " + Collections.emptyList() + " got "
						+ merger.getSectionsToDelete(), merger
						.getSectionsToDelete().equals(Collections.emptyList()));
	}

}
