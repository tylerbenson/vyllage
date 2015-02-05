package profile.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SectionType {
	FREEFORM("freeform"),
	EXPERIENCE("experience");
	
	
	private final String type;

	SectionType(String type){
		this.type = type;
	}
	
	@JsonValue 
	public String type() { 
		return type; 
	}
}
