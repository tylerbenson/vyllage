package documents.model.document.sections;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SummarySection extends DocumentSection {

	private String description;

	public SummarySection() {
		super();
		this.setType(SectionType.SUMMARY_SECTION.type());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
