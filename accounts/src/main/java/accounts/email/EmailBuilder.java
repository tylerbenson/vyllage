package accounts.email;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailBuilder {

	@Autowired
	private MailService mailService;

	private String email;
	private String from;
	private String subject;

	private String txt;
	private String templateName;

	private Map<String, Object> additionalVariables = new HashMap<>();

	public EmailBuilder to(String email) {
		this.email = email;
		return this;
	}

	public EmailBuilder from(String from) {
		this.from = from;
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
		EmailParameters parameters = new EmailParameters(from, subject, email);

		EmailContext ctx = new EmailContext(templateName);
		ctx.setVariables(additionalVariables);

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
