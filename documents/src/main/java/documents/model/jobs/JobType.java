package documents.model.jobs;

/**
 * Based on Indeed's job types.
 */
public enum JobType {
	fulltime("Full Time"), parttime("Part Time"), contract("Contract"), internship(
			"Internship"), temporary("Temporary");

	private final String value;

	JobType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
