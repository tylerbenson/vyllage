package documents.model.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SectionType {
	FREEFORM("freeform"), EXPERIENCE("experience"), ORGANIZATION("organization");

	private final String type;

	SectionType(String type) {
		this.type = type;
	}

	@JsonValue
	public String type() {
		return type;
	}
}
