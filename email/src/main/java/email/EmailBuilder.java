package email;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public class EmailBuilder {

	private MailService mailService;

	private Environment environment;

	private String email;
	private String from;
	private String fromUserName;
	private String subject;

	private String txt;
	private String templateName;

	private Map<String, Object> additionalVariables = new HashMap<>();

	private EmailBuilder() {
	}

	public EmailBuilder(MailService mailService, Environment environment) {
		this.mailService = mailService;
		this.environment = environment;
	}

	public EmailBuilder to(String email) {
		this.email = email;
		return this;
	}

	public EmailBuilder from(String from) {
		this.from = from;
		return this;
	}

	/**
	 * Optional, the user name to display.
	 *
	 * @param fromUserName
	 * @return
	 */
	public EmailBuilder fromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
		return this;
	}

	public EmailBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	public EmailBuilder setNoHtmlMessage(String txt) {
		this.txt = txt;
		return this;
	}

	public EmailBuilder templateName(String templateName) {
		this.templateName = templateName;
		return this;
	}

	public EmailBuilder addTemplateVariable(String key, Object value) {
		additionalVariables.put(key, value);
		return this;
	}

	public void send() throws EmailException {
		EmailParameters parameters = new EmailParameters(from, fromUserName,
				subject, email);

		EmailContext ctx = new EmailContext(templateName);
		ctx.setVariables(additionalVariables);

		// this is a bean and it should be picked automatically using @ before
		// the name in the template
		// this, of course doesn't work,
		// "there's no registered beanResolver" in the environment even though
		// it's there.

		Assert.notNull(environment);
		ctx.setVariable("environment", environment);

		EmailHTMLBody emailBody = new EmailHTMLBody(txt, ctx);

		mailService.sendEmail(parameters, emailBody);

		clean();

	}

	protected void clean() {
		from = null;
		subject = null;
		email = null;
		additionalVariables.clear();
	}

}
