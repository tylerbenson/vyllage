package documents.services.rules;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.Mergeable;

/**
 * Merges duplicate sections that can be merged.
 * 
 * @author uh
 *
 */
public class MergeOnSectionDuplicate {

	private List<DocumentSection> sectionsToDelete = Collections.emptyList();

	public void apply(@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		// clean
		sectionsToDelete = Collections.emptyList();

		// if we have other sections of the same type and can be merged
		if (documentSection instanceof Mergeable
				&& this.isSectionPresent(documentSection, documentSections)) {

			// we need to delete all the others
			this.addSectionsToDelete(documentSection, documentSections);

			// merge sections
			documentSections.forEach(ds -> {

				if (documentSection.getType().equals(ds.getType())) {
					((Mergeable) documentSection).merge(ds);
				}

			});
		}

	}

	/**
	 * Adds all the other sections of the same type to a list to delete later.
	 * 
	 * @param documentSection
	 * @param documentSections
	 */
	protected void addSectionsToDelete(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		sectionsToDelete = Collections.unmodifiableList(documentSections
				.stream()
				.filter(ds -> ds.getType().equals(documentSection.getType()))
				.collect(Collectors.toList()));

	}

	/**
	 * Checks if there are other sections of the same type on the given
	 * collection.
	 */
	protected boolean isSectionPresent(
			@NonNull final DocumentSection documentSection,
			@NonNull final List<DocumentSection> documentSections) {

		return documentSections.stream().anyMatch(
				ds -> ds.getType().equals(documentSection.getType()));
	}

	/**
	 * Whether we need to delete duplicate sections or not.
	 * 
	 * @return true|false
	 */
	public boolean haveToDeleteSections() {
		return !this.sectionsToDelete.isEmpty();
	}

	/**
	 * 
	 * @return unmodifiable list of sections that need to be deleted.
	 */
	public List<DocumentSection> getSectionsToDelete() {
		return this.sectionsToDelete;
	}
}
