package documents.model.document.sections;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JobExperienceSection extends EducationSection {
	// I probably don't need this class since they are the same...

	public JobExperienceSection() {
		// super(); nope
		this.setType(SectionType.JOB_EXPERIENCE_SECTION.type());
	}
}
