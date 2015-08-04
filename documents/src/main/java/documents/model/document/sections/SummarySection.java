package documents.model.document.sections;

import lombok.ToString;

@ToString(callSuper = true)
public class SummarySection extends DocumentSection {

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
