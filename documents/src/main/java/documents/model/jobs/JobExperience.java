package documents.model.jobs;

public enum JobExperience {
	fresh_graduate("Fresh Graduate");

	private final String experience;

	JobExperience(String experience) {
		this.experience = experience;
	}

	public String getValue() {
		return experience;
	}
}
