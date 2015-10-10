package documents.model.document.sections;

import static org.junit.Assert.*;

import org.junit.Test;

public class SummarySectionTest {
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	@Test
	public void testMergeDescription() {
		SummarySection summary = new SummarySection();
		SummarySection other = new SummarySection();

		String first = "Emperor of the Fading Suns.";
		String second = "Good person.";

		summary.setDescription(first);
		other.setDescription(second);

		summary.merge(other);

		String expected = first + LINE_SEPARATOR + second;

		assertTrue("Expected " + expected + " got " + summary.getDescription(),
				summary.getDescription().equals(expected));
	}

	@Test
	public void testMergeDescriptionFirstBlankSecondHasDescription() {
		SummarySection summary = new SummarySection();
		SummarySection other = new SummarySection();

		String first = "  ";
		String second = "Good person.";

		summary.setDescription(first);
		other.setDescription(second);

		summary.merge(other);

		String expected = second;

		assertTrue("Expected " + expected + " got " + summary.getDescription(),
				summary.getDescription().equals(expected));
	}

	@Test
	public void testMergeDescriptionFirstNullSecondHasDescription() {
		SummarySection summary = new SummarySection();
		SummarySection other = new SummarySection();

		String first = null;
		String second = "Good person.";

		summary.setDescription(first);
		other.setDescription(second);

		summary.merge(other);

		String expected = second;

		assertTrue("Expected " + expected + " got " + summary.getDescription(),
				summary.getDescription().equals(expected));
	}

	@Test
	public void testMergeDescriptionFirstHasDescriptionSecondIsBlank() {
		SummarySection summary = new SummarySection();
		SummarySection other = new SummarySection();

		String first = "Emperor of the Fading Suns.";
		String second = "   ";

		summary.setDescription(first);
		other.setDescription(second);

		summary.merge(other);

		String expected = first;

		assertTrue("Expected " + expected + " got " + summary.getDescription(),
				summary.getDescription().equals(expected));
	}

	@Test
	public void testMergeDescriptionFirstHasDescriptionSecondNull() {
		SummarySection summary = new SummarySection();
		SummarySection other = new SummarySection();

		String first = "Emperor of the Fading Suns.";
		String second = null;

		summary.setDescription(first);
		other.setDescription(second);

		summary.merge(other);

		String expected = first;

		assertTrue("Expected " + expected + " got " + summary.getDescription(),
				summary.getDescription().equals(expected));
	}
}
