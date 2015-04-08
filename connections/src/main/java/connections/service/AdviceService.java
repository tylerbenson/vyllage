package connections.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import connections.email.EmailContext;
import connections.email.EmailHTMLBody;
import connections.email.EmailParameters;
import connections.email.MailService;
import connections.model.AccountContact;
import connections.model.AccountNames;
import connections.model.AdviceRequest;
import connections.model.NotRegisteredUser;
import connections.model.UserFilterResponse;

@Service
public class AdviceService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MailService mailService;

	@Value("${accounts.host:localhost}")
	private final String ACCOUNTS_HOST = null;

	@Value("${accounts.port:8080}")
	private final Integer ACCOUNTS_PORT = null;

	@Value("${documents.host:localhost}")
	private final String DOCUMENTS_HOST = null;

	@Value("${documents.port:8080}")
	private final Integer DOCUMENTS_PORT = null;

	public UserFilterResponse getUsers(HttpServletRequest request,
			Long documentId, Long userId, List<Long> excludeIds) {
		UserFilterResponse response = new UserFilterResponse();
		// get recent users from Documents:
		// send my user id
		// receive map<userId, FirstName>
		// add each pair into the filter response

		Assert.notNull(userId);

		HttpEntity<Object> entity = addCookieToHeader(request);

		List<AccountNames> advisors = getAdvisors(userId, entity, excludeIds);

		Assert.notNull(advisors);

		response.setRecommended(advisors);

		List<AccountNames> recentUsers = getRecentUsers(documentId, entity,
				excludeIds);

		Assert.notNull(recentUsers);

		response.setRecent(recentUsers);

		return response;
	}

	protected HttpEntity<Object> addCookieToHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

	protected List<AccountNames> getRecentUsers(Long documentId,
			HttpEntity<Object> entity, List<Long> excludeIds) {

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
				.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/resume/" + documentId + "/recent-users");

		if (excludeIds != null && excludeIds.size() > 0)
			builder.queryParam(
					"excludeIds",
					excludeIds.stream().map(Object::toString)
							.collect(Collectors.joining(",")));

		AccountNames[] body = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				AccountNames[].class).getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

	protected List<AccountNames> getAdvisors(Long userId,
			HttpEntity<Object> entity, List<Long> excludeIds) {

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
				.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/account/" + userId + "/advisors");

		if (excludeIds != null && excludeIds.size() > 0)
			builder.queryParam(
					"excludeIds",
					excludeIds.stream().map(Object::toString)
							.collect(Collectors.joining(",")));

		AccountNames[] body = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				AccountNames[].class).getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

	public void sendRequestAdviceEmail(AdviceRequest adviceRequest)
			throws EmailException {

		String from = "no-reply@vyllage.com";
		String subject = "Could you provide me some feedback on my resume?";
		String msg = "I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me?";
		EmailParameters parameters = null;

		List<Email> mailsForRegisteredUsers = prepareMailsForRegisteredUsers(
				msg, adviceRequest);

		prepareMailsForNonRegisteredUsers(msg, adviceRequest);

		// send email to registered users
		for (Email email : mailsForRegisteredUsers) {
			parameters = new EmailParameters(from, subject, email.getTo());
			mailService.sendEmail(parameters, email.getBody());
		}
		// send email to added users

	}

	private List<Email> prepareMailsForRegisteredUsers(String msg,
			AdviceRequest adviceRequest) {

		List<Email> bodies = new ArrayList<>();

		for (AccountContact accountContact : adviceRequest
				.getRegisteredUsersContatData()) {
			Email email = new Email();

			EmailContext ctx = new EmailContext("email-advice-request");
			ctx.setVariable("recipientName", accountContact.getFirstName());
			ctx.setVariable("senderName", adviceRequest.getSenderName());
			ctx.setVariable("link", "");

			EmailHTMLBody emailBody = new EmailHTMLBody(msg, ctx);

			email.setBody(emailBody);
			email.setTo(accountContact.getEmail());

			bodies.add(email);
		}

		return bodies;
	}

	private List<Email> prepareMailsForNonRegisteredUsers(String msg,
			AdviceRequest adviceRequest) {

		List<Email> bodies = new ArrayList<>();

		for (NotRegisteredUser user : adviceRequest.getNotRegisteredUsers()) {
			Email email = new Email();

			EmailContext ctx = new EmailContext("email-advice-request");
			ctx.setVariable("recipientName", user.getFirstName());
			ctx.setVariable("senderName", adviceRequest.getSenderName());
			ctx.setVariable("link", "");

			EmailHTMLBody emailBody = new EmailHTMLBody(msg, ctx);

			email.setBody(emailBody);
			email.setTo(user.getEmail());
			bodies.add(email);
		}

		return bodies;
	}

	protected class Email {
		private String to;
		private EmailHTMLBody body;

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public EmailHTMLBody getBody() {
			return body;
		}

		public void setBody(EmailHTMLBody body) {
			this.body = body;
		}
	}

}
