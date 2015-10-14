package documents.services.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

		List<DocumentSection> toDelete = merger
				.apply(section, documentSections);

		assertTrue("Did not expect having to delete sections.",
				toDelete != null && toDelete.isEmpty());

		assertTrue("Expected: " + Collections.emptyList() + " got " + toDelete,
				toDelete.equals(Collections.emptyList()));

	}

	@Test
	public void testApplyEmptyCollection() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));

		List<DocumentSection> documentSections = Collections.emptyList();

		List<DocumentSection> toDelete = merger
				.apply(section, documentSections);

		assertTrue("Expected not having to delete sections.", toDelete != null
				&& toDelete.isEmpty());

		assertTrue("Expected: " + Collections.emptyList() + " got " + toDelete,
				toDelete.equals(Collections.emptyList()));

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

		List<DocumentSection> toDelete = merger
				.apply(section, documentSections);

		assertTrue("Expected not having to delete sections.", toDelete != null
				&& toDelete.isEmpty());

		assertTrue("Expected: " + Collections.emptyList() + " got " + toDelete,
				toDelete.equals(Collections.emptyList()));

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
	public void testIsSectionTypePresentTrue() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other = new SkillsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long sectionId = 42L;
		section.setSectionId(sectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		assertTrue("Expected to find another section of the same type.",
				merger.isSectionTypePresent(section, documentSections));

	}

	@Test
	public void testIsSectionTypePresentDifferentTypesFalse() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		assertFalse("Did not expect to find another section of the same type.",
				merger.isSectionTypePresent(section, documentSections));

	}

	// @Test
	// public void testIsSectionTypePresentTrue() {
	// MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();
	//
	// SkillsSection section = new SkillsSection();
	// SkillsSection present = new SkillsSection();
	//
	// Long sectionId = 42L;
	// section.setSectionId(sectionId);
	// section.setTags(Lists.newArrayList("one", "two", "Java"));
	//
	// present.setSectionId(sectionId);
	// present.setTags(Lists.newArrayList("one", "two"));
	//
	// List<DocumentSection> documentSections = Arrays.asList(present);
	//
	// assertFalse("Expected to find another section of the same type.",
	// merger.isSectionTypePresent(section, documentSections));
	//
	// }

	@Test(expected = IllegalArgumentException.class)
	public void testIsSectionTypePresentSectionIsNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = null;
		CareerInterestsSection other = new CareerInterestsSection();

		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		merger.isSectionTypePresent(section, documentSections);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsSectionTypePresentCollectionIsNull() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();

		List<DocumentSection> documentSections = null;

		merger.isSectionTypePresent(section, documentSections);
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

		List<DocumentSection> toDelete = merger.addSectionsToDelete(section,
				documentSections);

		assertFalse("Expected having to delete sections.", toDelete != null
				&& toDelete.isEmpty());

		assertTrue("Expected: " + other + " got " + toDelete,
				toDelete.contains(other));
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

		List<DocumentSection> toDelete = merger.addSectionsToDelete(section,
				documentSections);

		assertTrue("Expected not having to delete sections.", toDelete != null
				&& toDelete.isEmpty());

		assertTrue("Expected: " + Collections.emptyList() + " got " + toDelete,
				toDelete.equals(Collections.emptyList()));
	}

	@Test
	public void testOnlyOneButDifferentIdsTrue() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other = new SkillsSection();

		section.setSectionId(50L);
		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));
		Long otherSectionId = 42L;
		other.setSectionId(otherSectionId);

		List<DocumentSection> documentSections = Arrays.asList(other);

		boolean onlyOneButDifferentIds = merger.onlyOneButDifferentIds(section,
				documentSections);

		assertTrue("Ids are different should have returned true.",
				onlyOneButDifferentIds);

	}

	@Test
	public void testOnlyOneButDifferentIdsFalse() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other = new SkillsSection();
		Long sectionId = 42L;

		section.setSectionId(sectionId);
		other.setSectionId(sectionId);

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));

		List<DocumentSection> documentSections = Arrays.asList(other);

		boolean onlyOneButDifferentIds = merger.onlyOneButDifferentIds(section,
				documentSections);

		assertFalse("Ids are the smae should have returned false.",
				onlyOneButDifferentIds);

	}

	@Test
	public void testTheresMoreThanOneTrue() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other1 = new SkillsSection();
		SkillsSection other2 = new SkillsSection();

		List<DocumentSection> documentSections = Arrays.asList(other1, other2);

		boolean theresMoreThanOne = merger.theresMoreThanOne(section,
				documentSections);

		assertTrue("There's more than one, should have returned true.",
				theresMoreThanOne);

	}

	@Test
	public void testTheresMoreThanOneFalse() {
		MergeOnSectionDuplicate merger = new MergeOnSectionDuplicate();

		SkillsSection section = new SkillsSection();
		SkillsSection other1 = new SkillsSection();

		List<DocumentSection> documentSections = Arrays.asList(other1);

		boolean theresMoreThanOne = merger.theresMoreThanOne(section,
				documentSections);

		assertFalse("There's only one, should have returned false.",
				theresMoreThanOne);

	}

}
