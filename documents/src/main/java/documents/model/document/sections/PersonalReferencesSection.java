package documents.model.document.sections;

import java.util.List;

import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
public class PersonalReferencesSection extends DocumentSection {

	private List<ContactReference> references;

	public PersonalReferencesSection() {
		this.setType(SectionType.PERSONAL_REFERENCES_SECTION.type());
	}

	public List<ContactReference> getReferences() {
		return references;
	}

	public void setReferences(List<ContactReference> references) {
		this.references = references;
	}

}
