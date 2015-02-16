package connections.email;

import org.apache.commons.mail.EmailException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import connections.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EmailTest {

	@Autowired
	MailService service;

	@Test
	public void test() throws EmailException {
		String from = "carlos.uh@gmail.com";
		String subject = "TestMail";
		String msg = "This is a test mail ... :-)";
		String to = "carlos.uh@gmail.com";
		EmailParameters parameters = new EmailParameters(from, subject, msg, to);

		EmailBody emailBody = new EmailBody(msg);

		service.sendEmail(parameters, emailBody);
	}

}
