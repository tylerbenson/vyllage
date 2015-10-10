package documents.model.document.sections;

public interface Mergeable {

	public String getType();

	public void merge(DocumentSection documentSection);

}
