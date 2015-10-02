package documents.model.document.sections;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CareerInterestsSection extends DocumentSection {

	private List<String> tags = new ArrayList<>();

	public CareerInterestsSection() {
		super();
		this.setType(SectionType.CAREER_INTERESTS_SECTION.type());
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
