package documents.services.rezscore.result;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RezscoreResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4324359553068600705L;
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
