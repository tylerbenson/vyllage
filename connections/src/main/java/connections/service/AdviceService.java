package connections.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import lombok.ToString;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import user.common.User;

import com.newrelic.api.agent.NewRelic;

import connections.model.AccountContact;
import connections.model.AccountNames;
import connections.model.AdviceRequestParameter;
import connections.model.DocumentLinkRequest;
import connections.model.NotRegisteredUser;
import connections.model.UserFilterResponse;
import connections.repository.ElementNotFoundException;
import email.EmailContext;
import email.EmailHTMLBody;
import email.EmailParameters;
import email.MailService;

@Service
public class AdviceService {

	private final Logger logger = Logger.getLogger(AdviceService.class
			.getName());

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier(value = "connections.MailService")
	private MailService mailService;

	@Value("${accounts.host:localhost}")
	private final String ACCOUNTS_HOST = null;

	@Value("${accounts.port:8080}")
	private final Integer ACCOUNTS_PORT = null;

	@Value("${documents.host:localhost}")
	private final String DOCUMENTS_HOST = null;

	@Value("${documents.port:8080}")
	private final Integer DOCUMENTS_PORT = null;

	@Autowired
	@Qualifier(value = "connections.ExecutorService")
	private ExecutorService executorService;

	public UserFilterResponse getUsers(HttpServletRequest request,
			Long documentId, Long userId, List<Long> excludeIds,
			String firstNameFilter, String lastNameFilter, String emailFilter) {
		UserFilterResponse response = new UserFilterResponse();

		Assert.notNull(userId);

		HttpEntity<Object> entity = createHeader(request);

		// Returns a list of names and ids of users that have the role 'Advisor'
		// within my organization.
		List<AccountNames> advisors = getAdvisors(userId, entity, excludeIds,
				firstNameFilter, lastNameFilter, emailFilter);

		Assert.notNull(advisors);

		response.setRecommended(advisors);

		// Returns a list of names and ids of users that commented on my resume.
		List<AccountNames> recentUsers = getRecentUsers(documentId, entity,
				excludeIds);

		Assert.notNull(recentUsers);

		response.setRecent(recentUsers);

		return response;
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
			HttpEntity<Object> entity, List<Long> excludeIds,
			String firstNameFilter, String lastNameFilter, String emailFilter) {

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
				.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/account/" + userId + "/advisors");

		if (excludeIds != null && excludeIds.size() > 0)
			builder.queryParam(
					"excludeIds",
					excludeIds.stream().map(Object::toString)
							.collect(Collectors.joining(",")));

		if (firstNameFilter != null && !firstNameFilter.isEmpty())
			builder.queryParam("firstNameFilter", firstNameFilter);

		if (lastNameFilter != null && !lastNameFilter.isEmpty())
			builder.queryParam("lastNameFilter", lastNameFilter);

		if (emailFilter != null && !emailFilter.isEmpty())
			builder.queryParam("emailFilter", emailFilter);

		AccountNames[] body = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				AccountNames[].class).getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

	public void sendRequestAdviceEmail(HttpServletRequest request,
			AdviceRequestParameter adviceRequest, User loggedInUser)
			throws EmailException {

		String from = environment.getProperty("email.from",
				"no-reply@vyllage.com");

		String userFirstName = loggedInUser.getFirstName();

		// default.
		String fromUser = environment.getProperty("email.from",
				"no-reply@vyllage.com");
		String atVyllage = environment.getProperty("email.from.user");

		if (userFirstName != null && !userFirstName.isEmpty()
				&& atVyllage != null) {

			fromUser = MessageFormat.format(atVyllage, userFirstName);
		}

		String subject = adviceRequest.getSubject();
		String noHTMLmsg = adviceRequest.getMessage();

		// generate document links
		if (!adviceRequest.getRegisteredUsersContactData().isEmpty()) {
			Map<String, String> linksForRegisteredUsers = generateLinksForRegisteredUsers(
					request, adviceRequest.getUserId(), adviceRequest);

			// prepare emails
			List<Email> mailsForRegisteredUsers = prepareMailsForRegisteredUsers(
					linksForRegisteredUsers, noHTMLmsg, adviceRequest);

			sendAsyncEmail(from, fromUser, subject, mailsForRegisteredUsers);
		}

		if (!adviceRequest.getNotRegisteredUsers().isEmpty()) {
			Map<String, String> linksForNonRegisteredUsers = generateLinksForNonRegisteredUsers(
					request, adviceRequest.getUserId(), adviceRequest);

			List<Email> prepareMailsForNonRegisteredUsers = prepareMailsForNonRegisteredUsers(
					linksForNonRegisteredUsers, noHTMLmsg, adviceRequest);

			// send email to added users
			sendAsyncEmail(from, fromUser, subject,
					prepareMailsForNonRegisteredUsers);
		}
	}

	private void sendAsyncEmail(final String from, final String fromUser,
			final String subject,
			final List<Email> prepareMailsForNonRegisteredUsers) {

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				for (Email email : prepareMailsForNonRegisteredUsers) {
					EmailParameters parameters = new EmailParameters(from,
							fromUser, subject, email.getTo());
					try {
						mailService.sendEmail(parameters, email.getBody());
					} catch (EmailException e) {
						logger.severe(ExceptionUtils.getStackTrace(e));
						NewRelic.noticeError(e);
					}
				}

			}
		});
	}

	protected Map<String, String> generateLinksForRegisteredUsers(
			HttpServletRequest request, Long userId,
			AdviceRequestParameter adviceRequest) {

		List<DocumentLinkRequest> requestBody = new ArrayList<>();

		for (AccountContact accountContact : adviceRequest
				.getRegisteredUsersContactData()) {

			DocumentLinkRequest linkRequest = new DocumentLinkRequest();
			linkRequest.setDocumentId(adviceRequest.getDocumentId());
			linkRequest.setDocumentType("resume");

			linkRequest
					.setExpirationDate(adviceRequest.getLinkExpirationDate() != null ? adviceRequest
							.getLinkExpirationDate() : LocalDateTime.now()
							.plusDays(30L));

			linkRequest.setEmail(accountContact.getEmail());

			requestBody.add(linkRequest);
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

	protected Map<String, String> generateLinksForNonRegisteredUsers(
			HttpServletRequest request, Long userId,
			AdviceRequestParameter adviceRequest) {

		List<DocumentLinkRequest> requestBody = new ArrayList<>();

		for (NotRegisteredUser notRegisteredUser : adviceRequest
				.getNotRegisteredUsers()) {
			DocumentLinkRequest linkRequest = new DocumentLinkRequest();
			linkRequest.setSendRegistrationMail(false);
			linkRequest.setDocumentId(adviceRequest.getDocumentId());
			linkRequest.setDocumentType("resume");

			linkRequest
					.setExpirationDate(adviceRequest.getLinkExpirationDate() != null ? adviceRequest
							.getLinkExpirationDate() : LocalDateTime.now()
							.plusDays(30L));

			// to create the user
			linkRequest.setFirstName(notRegisteredUser.getFirstName());
			linkRequest.setLastName(notRegisteredUser.getLastName());
			linkRequest.setEmail(notRegisteredUser.getEmail());

			requestBody.add(linkRequest);
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

	private List<Email> prepareMailsForRegisteredUsers(
			Map<String, String> linksForRegisteredUsers, String msg,
			AdviceRequestParameter adviceRequest) {

		List<Email> bodies = new ArrayList<>();

		for (AccountContact accountContact : adviceRequest
				.getRegisteredUsersContactData()) {
			Email email = new Email();

			EmailContext ctx = new EmailContext("email-advice-request");
			ctx.setVariable("recipientName", accountContact.getFirstName());
			ctx.setVariable("senderName", adviceRequest.getSenderName());
			ctx.setVariable("message", adviceRequest.getMessage());
			ctx.setVariable(
					"link",
					"http://"
							+ environment.getProperty("vyllage.domain",
									"www.vyllage.com")
							+ linksForRegisteredUsers.get(accountContact
									.getEmail()));

			// this is a bean and it should be picked automatically using @
			// before
			// the name in the template
			// this, of course doesn't work,
			// "there's no registered beanResolver" in the environment even
			// though
			// it's there.
			ctx.setVariable("environment", environment);

			EmailHTMLBody emailBody = new EmailHTMLBody(msg, ctx);

			email.setBody(emailBody);
			email.setTo(accountContact.getEmail());

			bodies.add(email);
		}

		return bodies;
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
			ctx.setVariable("message", adviceRequest.getMessage());
			ctx.setVariable(
					"link",
					"http://"
							+ environment.getProperty("vyllage.domain",
									"www.vyllage.com")
							+ linksForNonRegisteredUsers.get(user.getEmail()));

			// this is a bean and it should be picked automatically using @
			// before
			// the name in the template
			// this, of course doesn't work,
			// "there's no registered beanResolver" in the environment even
			// though
			// it's there.
			ctx.setVariable("environment", environment);

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

	protected HttpEntity<Object> createHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

	/**
	 * Returns the user document Id.
	 * 
	 * @param request
	 * 
	 * @param userId
	 * @return
	 * @throws ElementNotFoundException
	 */
	public Long getUserDocumentId(HttpServletRequest request, Long userId)
			throws ElementNotFoundException {

		HttpEntity<Object> entity = createHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/document/user").queryParam("userId", userId);

		@SuppressWarnings("unchecked")
		List<Integer> responseBody = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				List.class).getBody();

		if (responseBody.isEmpty())
			throw new ElementNotFoundException(
					"No documents found for user with id '" + userId + "'");

		// TODO: currently the user only has one document, this might change in
		// the future but for now we return the first document id he has
		return new Long(responseBody.get(0));
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
