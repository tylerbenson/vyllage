package profile.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Visibility {
	SHOWN("shown"),
	HIDDEN("hidden");
	
	private final String visibility;

	private Visibility(String visibility) {
		this.visibility =visibility;
	}
	
	@JsonValue 
	public String visibility() {
		return visibility;
	}

}
