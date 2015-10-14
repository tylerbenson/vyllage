package documents.services.rules;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import lombok.NonNull;
import documents.model.constants.SectionType;
import documents.model.document.sections.DocumentSection;

/**
 * Sets the section position as the first one after the Summary section. If no
 * other section exists then it's set as the first one.
 * 
 * @author uh
 */
public class SetSectionPositionOnCreationUpdate {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(SetSectionPositionOnCreationUpdate.class.getName());

	/**
	 * Checks if other sections exist and sets the received section position
	 * before them, displacing the rest, or as the first one if none exist.
	 * 
	 * @param documentSection
	 */
	public List<DocumentSection> apply(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		final long first = 1;
		final long sectionPosition;

		if (documentSections.isEmpty()) {
			documentSection.setSectionPosition(first);
			return Collections.emptyList();
		}

		if (documentSection.getType().equalsIgnoreCase(
				SectionType.SUMMARY_SECTION.type())) {

			documentSection.setSectionPosition(first);

			// shift others to be right behind summary

			long afterFirst = first + 1;
			return this.sortAndShiftByOneFrom(documentSections, afterFirst);
		}

		// not present
		final boolean sectionIsNotPresent = documentSections.stream()
				.noneMatch(
						ds -> ds.getSectionId().equals(
								documentSection.getSectionId()));

		// Check if Summary section exists
		Optional<DocumentSection> summarySection = documentSections
				.stream()
				.filter(ds -> SectionType.SUMMARY_SECTION.type().equals(
						ds.getType())).findFirst();

		if (summarySection.isPresent())
			sectionPosition = summarySection.get().getSectionPosition() + 1;
		else
			sectionPosition = 1;

		if (sectionIsNotPresent) {

			// Insert the new section behind the Summary.
			documentSection.setSectionPosition(sectionPosition);

			// Sort and shift by one all but Summary after the new section.
			return this.sortAndShiftByOneFrom(documentSections,
					sectionPosition + 1);
		} else {
			/**
			 * If the section already exists we change it's position back to the
			 * one it previously had in case the client tried setting it to
			 * another value.
			 */

			documentSections
					.stream()
					.filter(ds -> ds.getSectionId().equals(
							documentSection.getSectionId()))
					.forEach(
							ds -> documentSection.setSectionPosition(ds
									.getSectionPosition()));

			return Collections.emptyList();

		}
	}

	/**
	 * Sort sections and shift by one all except Summary.
	 * 
	 * @param documentSections
	 * @param position
	 *            the position to set from.
	 * @return list of sections to update position
	 */
	protected List<DocumentSection> sortAndShiftByOneFrom(
			final List<DocumentSection> documentSections, long position) {
		documentSections.sort(sort());

		for (DocumentSection ds : documentSections)
			// Summary is always first.
			if (!ds.getType().equalsIgnoreCase(
					SectionType.SUMMARY_SECTION.type()))
				ds.setSectionPosition(position++);

		return documentSections;
	}

	/**
	 * Sorts all sections by their position value.
	 * 
	 * @return
	 */
	protected Comparator<? super DocumentSection> sort() {
		return (s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition());
	}

}
