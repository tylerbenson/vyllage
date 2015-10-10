package documents.services.rules;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.logging.Logger;

import lombok.NonNull;
import documents.model.constants.SectionType;
import documents.model.document.sections.DocumentSection;

/**
 * Sets the section position as the first one after the Summary section. If no
 * other section exists then it's set as the first one.
 * 
 * @author uh
 *
 */
public class SetSectionPositionOnCreationUpdate {

	private final Logger logger = Logger
			.getLogger(SetSectionPositionOnCreationUpdate.class.getName());

	/**
	 * Checks if other sections exist and sets the received section position
	 * before them, displacing the rest, or as the first one if none exist.
	 * 
	 * @param documentSection
	 */
	public DocumentSection apply(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		final long first = 1;
		final long sectionPosition;

		if (documentSections.isEmpty()) {
			documentSection.setSectionPosition(first);
			return documentSection;
		}

		if (documentSection.getType().equalsIgnoreCase(
				SectionType.SUMMARY_SECTION.type())) {

			documentSection.setSectionPosition(first);
			return documentSection;
		}

		documentSections.stream().forEachOrdered(
				s -> logger.info("Section " + s.getSectionId() + " P: "
						+ s.getSectionPosition()));

		// not present
		final boolean notPresent = documentSections.stream().noneMatch(
				ds -> ds.getSectionId().equals(documentSection.getSectionId()));

		// check if Summary section exists
		Optional<DocumentSection> summarySection = documentSections
				.stream()
				.filter(ds -> SectionType.SUMMARY_SECTION.type().equals(
						ds.getType())).findFirst();

		if (summarySection.isPresent()) {
			sectionPosition = summarySection.get().getSectionPosition() + 1;
		} else {
			sectionPosition = 1;
		}

		if (notPresent) {

			// sort and shift by one all but Summary.
			this.sortAndShiftByOne(documentSections);

			documentSection.setSectionPosition(sectionPosition);

			return documentSection;
		} else {
			// check if anyone has the same position,
			final Long currentPosition = documentSection.getSectionPosition();

			boolean anyMatch = documentSections.stream().anyMatch(
					ds -> ds.getSectionPosition().equals(currentPosition));

			// at least one has the same position
			if (anyMatch) {
				this.moveToLast(documentSection, documentSections);
				return documentSection;

			}
			// there's none with the same position, we can continue

			// sort and shift by one all but Summary.
			this.sortAndShiftByOne(documentSections);

			documentSection.setSectionPosition(sectionPosition);
			return documentSection;

		}
	}

	/**
	 * Sort sections and shift by one all except Summary.
	 * 
	 * @param documentSections
	 */
	protected void sortAndShiftByOne(
			final List<DocumentSection> documentSections) {
		documentSections
				.stream()
				.sorted(sort())
				//
				.forEach(
						s -> {

							if (!SectionType.SUMMARY_SECTION.type().equals(
									s.getType()))
								s.setSectionPosition(s.getSectionPosition() + 1L);

						});

		documentSections.stream().forEachOrdered(
				s -> logger.info("Sorted and Shifted Section "
						+ s.getSectionId() + " P: " + s.getSectionPosition()));
	}

	/**
	 * Sets the section position as the last one.
	 */
	protected long moveToLast(final DocumentSection documentSection,
			final List<DocumentSection> documentSections) {
		long sectionPosition = 1;

		OptionalLong max = documentSections.stream()
				.mapToLong(ds -> ds.getSectionPosition()).max();

		// move it to the last place
		if (max.isPresent()) {
			sectionPosition = max.getAsLong() + 1;
		}

		documentSection.setSectionPosition(sectionPosition);
		return sectionPosition;
	}

	/**
	 * Sorts all sections by their position.
	 * 
	 * @return
	 */
	protected Comparator<? super DocumentSection> sort() {
		return (s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition());
	}

}
