package documents.model.document.sections;

public interface Mergeable {

	/**
	 * Merges sections of the same type. Replaces properties of the first
	 * sections with ones from the other.
	 * 
	 * @param documentSection
	 */
	public void merge(DocumentSection other);

}
