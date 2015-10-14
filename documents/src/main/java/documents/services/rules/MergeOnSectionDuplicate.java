package documents.services.rules;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.NonNull;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.Mergeable;

/**
 * Merges duplicate sections that can be merged.
 * 
 * @author uh
 */
public class MergeOnSectionDuplicate {

	/**
	 * Merges duplicate sections that can be merged. If there are other sections
	 * of the same type already present then we merge the one with lowest id
	 * with ours.
	 * 
	 * @return unmodifiable list of sections that need to be deleted.
	 */
	public List<DocumentSection> apply(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		List<DocumentSection> sectionsToDelete = Collections.emptyList();

		// if we have other sections of the same type and can be merged
		if (documentSection instanceof Mergeable
				&& this.isSectionTypePresent(documentSection, documentSections)) {

			Comparator<? super DocumentSection> minId = (DocumentSection ds1,
					DocumentSection ds2) -> ds1.getSectionId().compareTo(
					ds2.getSectionId());

			// get the section of the same type with lowest id
			Optional<DocumentSection> existingDocumentSection = documentSections
					.stream()
					.filter(ds -> ds.getType()
							.equals(documentSection.getType())).min(minId);

			if (existingDocumentSection.isPresent()) {
				((Mergeable) documentSection).merge(existingDocumentSection
						.get());
				documentSection.setSectionId(existingDocumentSection.get()
						.getSectionId());
			}

			// merge with all other sections

			documentSections.forEach(ds -> {
				// don't merge existing sections with the same id since we
				// already did.
					if (documentSection.getType().equals(ds.getType())
							&& !ds.getSectionId().equals(documentSection)) {
						((Mergeable) documentSection).merge(ds);
					}

				});

			// add to delete all the others
			sectionsToDelete = this.addSectionsToDelete(documentSection,
					documentSections);

		}

		return sectionsToDelete;
	}

	/**
	 * Adds all the other sections of the same type to a list to delete later.
	 * Ignoring the one we are saving.
	 * 
	 * @param documentSection
	 * @param documentSections
	 */
	protected final List<DocumentSection> addSectionsToDelete(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		Predicate<? super DocumentSection> sameTypeDifferentId = ds -> ds
				.getType().equals(documentSection.getType())
				&& !ds.getSectionId().equals(documentSection.getSectionId());

		return Collections.unmodifiableList(documentSections.stream()
				.filter(sameTypeDifferentId).collect(Collectors.toList()));

	}

	/**
	 * Checks if there are other sections of the same type on the given
	 * collection.
	 */
	protected boolean isSectionTypePresent(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		Predicate<? super DocumentSection> sameType = ds -> ds.getType()
				.equals(documentSection.getType());

		return documentSections.stream().anyMatch(sameType);
	}
}
