package connections.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import lombok.ToString;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
import connections.model.AdviceRequestParameter;
import connections.model.DocumentLinkRequest;
import connections.model.NotRegisteredUser;
import connections.model.UserFilterResponse;

@Service
public class AdviceService {

	@Autowired
	private Environment env;

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

		Assert.notNull(userId);

		HttpEntity<Object> entity = addCookieToHeader(request);

		// Returns a list of names and ids of users that have the role 'Advisor'
		// within my organization.
		List<AccountNames> advisors = getAdvisors(userId, entity, excludeIds);

		Assert.notNull(advisors);

		response.setRecommended(advisors);

		// Returns a list of names and ids of users that commented on my resume.
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

	public void sendRequestAdviceEmail(HttpServletRequest request,
			AdviceRequestParameter adviceRequest) throws EmailException {

		String from = "no-reply@vyllage.com";
		String subject = "Could you provide me some feedback on my resume?";
		String noHTMLmsg = "I could really use your assistance on giving me some career or resume advice. Do you think you could take a couple of minutes and look over this for me?";
		EmailParameters parameters = null;

		// generate document links
		Map<String, String> linksForRegisteredUsers = generateLinksForRegisteredUsers(
				request, adviceRequest.getUserId(), adviceRequest);

		Map<String, String> linksForNonRegisteredUsers = generateLinksForRegisteredUsers(
				request, adviceRequest.getUserId(), adviceRequest);

		// prepare emails
		List<Email> mailsForRegisteredUsers = prepareMailsForRegisteredUsers(
				linksForRegisteredUsers, noHTMLmsg, adviceRequest);

		List<Email> prepareMailsForNonRegisteredUsers = prepareMailsForNonRegisteredUsers(
				linksForNonRegisteredUsers, noHTMLmsg, adviceRequest);

		// send email to registered users
		for (Email email : mailsForRegisteredUsers) {
			parameters = new EmailParameters(from, subject, email.getTo());
			mailService.sendEmail(parameters, email.getBody());
		}

		// send email to added users
		for (Email email : prepareMailsForNonRegisteredUsers) {
			parameters = new EmailParameters(from, subject, email.getTo());
			mailService.sendEmail(parameters, email.getBody());
		}

	}

	private List<Email> prepareMailsForRegisteredUsers(
			Map<String, String> links, String msg,
			AdviceRequestParameter adviceRequest) {

		List<Email> bodies = new ArrayList<>();

		for (AccountContact accountContact : adviceRequest
				.getRegisteredUsersContactData()) {
			Email email = new Email();

			EmailContext ctx = new EmailContext("email-advice-request");
			ctx.setVariable("recipientName", accountContact.getFirstName());
			ctx.setVariable("senderName", adviceRequest.getSenderName());
			ctx.setVariable(
					"link",
					"http://"
							+ env.getProperty("vyllage.domain",
									"www.vyllage.com")
							+ links.get(accountContact.getEmail()));

			EmailHTMLBody emailBody = new EmailHTMLBody(msg, ctx);

			email.setBody(emailBody);
			email.setTo(accountContact.getEmail());

			bodies.add(email);
		}

		return bodies;
	}

	private Map<String, String> generateLinksForRegisteredUsers(
			HttpServletRequest request, Long userId,
			AdviceRequestParameter adviceRequest) {

		List<DocumentLinkRequest> requestBody = new ArrayList<>();

		for (AccountContact accountContact : adviceRequest
				.getRegisteredUsersContactData()) {

			System.out.println(accountContact);
			DocumentLinkRequest linkRequest = new DocumentLinkRequest();
			linkRequest.setDocumentId(adviceRequest.getDocumentId());
			linkRequest.setDocumentType("resume");

			linkRequest
					.setExpirationDate(adviceRequest.getLinkExpirationDate() != null ? adviceRequest
							.getLinkExpirationDate() : LocalDateTime.now()
							.plusDays(30L));

			linkRequest.setName(accountContact.getEmail());
			linkRequest.setEmail(accountContact.getEmail());

			requestBody.add(linkRequest);
			System.out.println(requestBody);
		}

		HttpEntity<Object> entity = createHeader(requestBody, request,
				adviceRequest);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/link/create-many");

		@SuppressWarnings("unchecked")
		Map<String, String> responseBody = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.POST, entity,
				Map.class).getBody();

		return responseBody;
	}

	private List<Email> prepareMailsForNonRegisteredUsers(
			Map<String, String> linksForNonRegisteredUsers, String msg,
			AdviceRequestParameter adviceRequest) {

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

	protected HttpEntity<Object> createHeader(Object object,
			HttpServletRequest request, AdviceRequestParameter adviceRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-CSRF-TOKEN", adviceRequest.getCSRFToken().getValue());
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(object, headers);
		return entity;
	}

	@ToString
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
