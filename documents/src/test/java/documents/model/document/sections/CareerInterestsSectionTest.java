package documents.model.document.sections;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class CareerInterestsSectionTest {

	@Test
	public void testMergeSkills() {
		CareerInterestsSection section = new CareerInterestsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList("one", "two", "three"));

		section.merge(other);

		List<String> expected = Arrays.asList("one", "two", "three", "Java");

		assertTrue("Tags not merged correctly. Expected: " + expected
				+ " got: " + section.getTags(),
				expected.containsAll(section.getTags()));
	}

	@Test
	public void testMergeSkillsFirstHasSkillsOtherEmpty() {
		CareerInterestsSection section = new CareerInterestsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(Lists.newArrayList());

		section.merge(other);

		List<String> expected = Arrays.asList("one", "two", "Java");

		assertTrue("Tags not merged correctly. Expected: " + expected
				+ " got: " + section.getTags(),
				expected.containsAll(section.getTags()));
	}

	@Test
	public void testMergeSkillsFirstEmptyOtherHasSkills() {
		CareerInterestsSection section = new CareerInterestsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList());
		other.setTags(Lists.newArrayList("one", "two", "three"));

		section.merge(other);

		List<String> expected = Arrays.asList("one", "two", "three");

		assertTrue("Tags not merged correctly. Expected: " + expected
				+ " got: " + section.getTags(),
				expected.containsAll(section.getTags()));
	}

	@Test
	public void testMergeSkillsFirstNullOtherHasSkills() {
		CareerInterestsSection section = new CareerInterestsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(null);
		other.setTags(Lists.newArrayList("one", "two", "three"));

		section.merge(other);

		List<String> expected = Arrays.asList("one", "two", "three");

		assertTrue("Tags not merged correctly. Expected: " + expected
				+ " got: " + section.getTags(),
				expected.containsAll(section.getTags()));
	}

	@Test
	public void testMergeSkillsFirstHasSkilssOtherNull() {
		CareerInterestsSection section = new CareerInterestsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(Lists.newArrayList("one", "two", "Java"));
		other.setTags(null);

		section.merge(other);

		List<String> expected = Arrays.asList("one", "two", "Java");

		assertTrue("Tags not merged correctly. Expected: " + expected
				+ " got: " + section.getTags(),
				expected.containsAll(section.getTags()));
	}

	@Test
	public void testMergeSkillsBothNull() {
		CareerInterestsSection section = new CareerInterestsSection();
		CareerInterestsSection other = new CareerInterestsSection();

		section.setTags(null);
		other.setTags(null);

		section.merge(other);

		List<String> expected = null;

		assertTrue("Tags not merged correctly. Expected: " + expected
				+ " got: " + section.getTags(), section.getTags() == null);
	}

}
