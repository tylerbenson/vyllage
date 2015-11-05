package documents.model.document.sections;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProfessionalReferencesSection extends DocumentSection {

	private List<ContactReference> references = new ArrayList<>();

	public ProfessionalReferencesSection() {
		super();
		this.setType(SectionType.PROFESSIONAL_REFERENCES_SECTION.type());
	}

	public List<ContactReference> getReferences() {
		return references;
	}

	public void setReferences(List<ContactReference> references) {
		this.references = references;
	}

}
