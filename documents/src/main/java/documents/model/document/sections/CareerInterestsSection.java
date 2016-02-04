package documents.model.document.sections;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CareerInterestsSection extends DocumentSection implements
		Mergeable {

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

	@Override
	public void merge(@NonNull final DocumentSection documentSection) {
		if (!(documentSection instanceof CareerInterestsSection))
			throw new UnsupportedOperationException("Cannot merge "
					+ documentSection.getClass() + " into " + this.getClass());

		final CareerInterestsSection other = (CareerInterestsSection) documentSection;

		if (this.getTags() == null && other.getTags() == null) {
			// nothing to do

		} else if (this.getTags() == null && other.getTags() != null) {
			this.tags = other.getTags();

		} else if (this.getTags() != null && other.getTags() != null) {

			// remove duplicates and add tags.
			this.tags.removeAll(other.getTags());
			this.tags.addAll(other.getTags());
		}
	}

	@Override
	public boolean isValid() {
		boolean valid = getTags() != null && !getTags().isEmpty();

		if (!valid)
			setError("Please add at least one Career Interest.");

		return valid;
	}

	@Override
	public String asTxt() {
		StringBuilder sb = new StringBuilder();
		sb.append("Career Interests");
		sb.append("\n\n");

		for (String tag : tags)
			sb.append("* " + tag).append(".\n");

		return sb.toString();
	}
}
