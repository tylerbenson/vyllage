package connections.email;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import connections.ApplicationTest;
import email.MailService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTest.class)
@WebAppConfiguration
public class EmailTest {

	@Inject
	@Qualifier("connections.MailService")
	private MailService service;

	// Replace this with your account to see the email.
	private String to = "no-reply@vyllage.com";

	@Test
	public void test() {

	}

	// Uncomment to test, sends real emails
	// @Test
	// public void simpleTextTest() throws EmailException {
	// String from = "no-reply@vyllage.com";
	// String subject = "TestMail";
	// String msg = "This is a test mail ... :-)";
	// String userName = "user";
	// EmailParameters parameters = new EmailParameters(from, userName,
	// subject, to);
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
	// String userName = "user";
	// EmailParameters parameters = new EmailParameters(from, userName,
	// subject, to);
	//
	// EmailContext ctx = new EmailContext("email");
	// ctx.setVariable("text", "World!");
	//
	// EmailHTMLBody emailBody = new EmailHTMLBody(msg, ctx);
	//
	// service.sendEmail(parameters, emailBody);
	// }

}
