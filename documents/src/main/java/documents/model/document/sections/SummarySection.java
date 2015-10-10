package documents.model.document.sections;

import org.jooq.tools.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import documents.model.constants.SectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SummarySection extends DocumentSection implements Mergeable {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
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

	@Override
	public void merge(@NonNull final DocumentSection documentSection) {
		if (!(documentSection instanceof SummarySection))
			throw new UnsupportedOperationException("Cannot merge "
					+ documentSection.getClass() + " into " + this.getClass());

		final SummarySection other = (SummarySection) documentSection;

		// Nothing to do.
		if (StringUtils.isBlank(other.getDescription()))
			return;

		if (StringUtils.isBlank(this.description))
			this.description = other.getDescription();

		else {
			this.description += LINE_SEPARATOR
					+ other.getDescription();
		}
	}

}
