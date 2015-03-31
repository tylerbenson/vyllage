package accounts.model;

import lombok.ToString;

@ToString
public class CSRFToken {
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
