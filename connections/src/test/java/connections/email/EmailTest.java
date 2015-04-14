package connections.email;

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
	private MailService service;

	// Replace this with your account to see the email.
	private String to = "no-reply@vyllage.com";

	@Test
	public void test() {

	}

	// @Test
	// public void simpleTextTest() throws EmailException {
	// String from = "no-reply@vyllage.com";
	// String subject = "TestMail";
	// String msg = "This is a test mail ... :-)";
	// EmailParameters parameters = new EmailParameters(from, subject, to);
	//
	// EmailBody emailBody = new EmailBody(msg);
	//
	// service.sendEmail(parameters, emailBody);
	// }
	//
	// @Test
	// public void htmlTest() throws EmailException {
	// String from = "no-reply@vyllage.com";
	// String subject = "TestMail";
	// String msg = "This is a test mail ... :-)";
	// EmailParameters parameters = new EmailParameters(from, subject, to);
	//
	// EmailContext ctx = new EmailContext("email");
	// ctx.setVariable("text", "World!");
	//
	// EmailHTMLBody emailBody = new EmailHTMLBody(msg, ctx);
	//
	// service.sendEmail(parameters, emailBody);
	// }

}
