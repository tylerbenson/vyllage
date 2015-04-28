package email;

import org.springframework.util.Assert;

public class EmailParameters {
	public final String from;
	public final String subject;
	public final String to;

	public EmailParameters(String from, String subject, String to) {
		super();
		this.from = from;
		this.subject = subject;
		this.to = to;

		Assert.notNull(from);
		Assert.notNull(subject);
		Assert.notNull(to);
	}

}
