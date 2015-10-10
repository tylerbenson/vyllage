package documents.model.document.sections;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SkillsSection extends DocumentSection implements Mergeable {

	private List<String> tags = new ArrayList<>();

	public SkillsSection() {
		super();
		this.setType(SectionType.SKILLS_SECTION.type());
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public void merge(@NonNull final DocumentSection documentSection) {
		if (!(documentSection instanceof SkillsSection))
			throw new UnsupportedOperationException("Cannot merge "
					+ documentSection.getClass() + " into " + this.getClass());

		final SkillsSection other = (SkillsSection) documentSection;

		// nothing to do.
		if (this.getTags() == null && other.getTags() == null)
			return;

		// nothing to do.
		if (other.getTags() == null || other.getTags().isEmpty())
			return;

		if (this.getTags() == null
				&& (other.getTags() != null || !other.getTags().isEmpty())) {
			this.tags = other.getTags();
			return;
		}

		// remove duplicates and add tags.
		this.tags.removeAll(other.getTags());
		this.tags.addAll(other.getTags());
	}

}
