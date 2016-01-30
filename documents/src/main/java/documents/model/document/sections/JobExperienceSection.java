package documents.model.document.sections;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JobExperienceSection extends EducationSection {

	public JobExperienceSection() {
		// super(); nope
		this.setType(SectionType.JOB_EXPERIENCE_SECTION.type());
	}

	@Override
	public String asTxt() {
		return super.asTxt().replace("Education", "Job Experience");
	}
}
