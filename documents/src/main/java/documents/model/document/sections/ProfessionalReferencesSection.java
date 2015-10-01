package documents.model.document.sections;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProfessionalReferencesSection extends DocumentSection {

	private List<ContactReference> references;

	public List<ContactReference> getReferences() {
		return references;
	}

	public void setReferences(List<ContactReference> references) {
		this.references = references;
	}

}
