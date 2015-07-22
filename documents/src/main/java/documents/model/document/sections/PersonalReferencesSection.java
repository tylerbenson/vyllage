package documents.model.document.sections;

import java.util.List;

import lombok.ToString;

@ToString(callSuper = true)
public class PersonalReferencesSection extends DocumentSection {

	private List<ContactReference> references;

	public List<ContactReference> getReferences() {
		return references;
	}

	public void setReferences(List<ContactReference> references) {
		this.references = references;
	}

}
