package documents.model.document.sections;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AchievementsSection extends DocumentSection {

	public AchievementsSection() {
		super();
		this.setType(SectionType.ACHIEVEMENTS_SECTION.type());
	}

	@Override
	public String asTxt() {
		return "";
	}

}
