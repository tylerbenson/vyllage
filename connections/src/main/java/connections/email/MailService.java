package connections.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class MailService {

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
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("carlos.uh@gmail.com",
				""));
		email.setSSLOnConnect(true);

		email.setFrom(parameters.from);
		email.setSubject(parameters.subject);
		email.setMsg(emailBody.txt);

		email.addTo(parameters.to);
		email.send();

	}

	public void sendHTMLEmail(EmailParameters parameters,
			EmailHTMLBody emailBody) throws EmailException {
		Assert.notNull(emailBody.html);
		Assert.isTrue(!emailBody.html.isEmpty());

		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("carlos.uh@gmail.com",
				""));
		email.setSSLOnConnect(true);

		email.setFrom(parameters.from);
		email.setSubject(parameters.subject);
		email.setHtmlMsg(emailBody.html);
		email.setTextMsg(emailBody.txt);

		email.addTo(parameters.to);

	}
}
