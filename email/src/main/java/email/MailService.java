package email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.util.Assert;
import org.thymeleaf.spring4.SpringTemplateEngine;

public class MailService {
	
	//@Value("${email.host}")
	private String hostName = "smtp.zoho.com";

	//@Value("${email.port}")
	private int port = 465;

	//@Value("${email.userName}")
	private String userName = "no-reply@vyllage.com";

	//@Value("${email.password}")
	private String password = "vyllage15";

	private SpringTemplateEngine templateEngine;

	public MailService(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	/**
	 * Sends a simple text mail.
	 * 
	 * @param parameters
	 * @param emailBody
	 * @throws EmailException
	 */
	public void sendEmail(EmailParameters parameters, EmailBody emailBody)
			throws EmailException {

		Assert.notNull(emailBody.txt);
		Assert.isTrue(!emailBody.txt.isEmpty());

		Email email = new SimpleEmail();
		config(parameters, email);

		email.setMsg(emailBody.txt);

		email.send();

	}

	/**
	 * Sends a html message.
	 * 
	 * @param parameters
	 * @param emailBody
	 * @throws EmailException
	 */
	public void sendEmail(EmailParameters parameters, EmailHTMLBody emailBody)
			throws EmailException {
		Assert.notNull(emailBody.ctx);

		HtmlEmail email = new HtmlEmail();
		config(parameters, email);

		email.setHtmlMsg(templateEngine.process(emailBody.ctx.templateName,
				emailBody.ctx));
		email.setTextMsg(emailBody.txt);
		email.send();
	}

	private void config(EmailParameters parameters, Email email)
			throws EmailException {
		email.setHostName(hostName);
		email.setSmtpPort(port);

		email.setAuthenticator(new DefaultAuthenticator(userName, password));
		email.setSSLOnConnect(true);

		if (parameters.fromUserName != null
				&& !parameters.fromUserName.isEmpty())
			email.setFrom(parameters.from, parameters.fromUserName);
		else
			email.setFrom(parameters.from);
		email.setSubject(parameters.subject);
		email.addTo(parameters.to);
	}
}
