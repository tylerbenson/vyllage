package documents.services.rezscore.result;

import javax.validation.constraints.NotNull;

import lombok.ToString;

@ToString
public class RezscoreResult {
	private final String resume;
	private final Rezscore rezscore;

	public RezscoreResult(@NotNull String resume, Rezscore rezscore) {
		this.resume = resume;
		this.rezscore = rezscore;

	}

	/**
	 * @return the original resume.
	 */
	public String getResume() {
		return resume;
	}

	/**
	 * @return Rezscore's analysis.
	 */
	public Rezscore getRezscore() {
		return rezscore;
	}

}
