package documents.model.jobs;

public enum JobExperience {
	// TODO: Add the rest of the values once they are specified.

	fresh_graduate("Fresh Graduate");

	private final String experience;

	JobExperience(String experience) {
		this.experience = experience;
	}

	public String getValue() {
		return experience;
	}
}
