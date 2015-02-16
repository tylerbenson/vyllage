package connections.email;

import org.springframework.util.Assert;

public class EmailParameters {
	public final String from;
	public final String subject;
	public final String msg;
	public final String to;

	public EmailParameters(String from, String subject, String msg, String to) {
		super();
		this.from = from;
		this.subject = subject;
		this.msg = msg;
		this.to = to;

		Assert.notNull(from);
		Assert.notNull(subject);
		Assert.notNull(msg);
		Assert.notNull(to);
	}

}
