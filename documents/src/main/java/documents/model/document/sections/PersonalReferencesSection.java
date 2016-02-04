package documents.model.document.sections;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
public class PersonalReferencesSection extends DocumentSection {

	private List<ContactReference> references = new ArrayList<>();

	public PersonalReferencesSection() {
		this.setType(SectionType.PERSONAL_REFERENCES_SECTION.type());
	}

	public List<ContactReference> getReferences() {
		return references;
	}

	public void setReferences(List<ContactReference> references) {
		this.references = references;
	}

	@Override
	public String asTxt() {
		// we don't need this for rezcore
		return "";
	}

}
