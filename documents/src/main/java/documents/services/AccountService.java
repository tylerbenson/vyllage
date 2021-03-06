package documents.services;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import user.common.web.AccountContact;
import util.web.constants.AccountUrlConstants;
import documents.model.AccountNames;
import documents.model.LinkPermissions;

@Service
public class AccountService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AccountService.class
			.getName());

	private RestTemplate restTemplate;

	@Value("${accounts.host:localhost}")
	private final String ACCOUNTS_HOST = "localhost";

	@Value("${accounts.port:8080}")
	private final Integer ACCOUNTS_PORT = 8080;

	@Inject
	public AccountService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<AccountNames> getNamesForUsers(HttpServletRequest request,
			List<Long> userIds) {
		Assert.notNull(userIds);
		Assert.notEmpty(userIds);

		HttpEntity<Object> entity = assembleHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/account/names");

		builder.queryParam("userIds", userIds.stream().map(Object::toString)
				.collect(Collectors.joining(",")));

		AccountNames[] body = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				AccountNames[].class).getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

	public List<AccountContact> getContactDataForUsers(
			HttpServletRequest request, List<Long> userIds) {
		Assert.notNull(userIds);
		Assert.notEmpty(userIds);

		HttpEntity<Object> entity = assembleHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/account/contact");

		builder.queryParam("userIds", userIds.stream().map(Object::toString)
				.collect(Collectors.joining(",")));

		AccountContact[] body = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				AccountContact[].class).getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

	public boolean usersBelongToSameOrganization(HttpServletRequest request,
			Long firstUserId, Long secondUserId) {

		HttpEntity<Object> entity = assembleHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path(AccountUrlConstants.ADMIN_USER_SAME_ORGANIZATION);

		builder.queryParam("firstUserId", firstUserId).queryParam(
				"secondUserId", secondUserId);

		boolean body = restTemplate.exchange(builder.build().toUriString(),
				HttpMethod.GET, entity, Boolean.class).getBody();

		return body;
	}

	public LinkPermissions getLinkInformation(HttpServletRequest request,
			String documentLinkKey) {

		HttpEntity<Object> entity = assembleHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/link/permissions/" + documentLinkKey);

		LinkPermissions body = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				LinkPermissions.class).getBody();

		return body;
	}

	public boolean isEmailVerified(HttpServletRequest request, Long userId) {

		HttpEntity<Object> entity = assembleHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(ACCOUNTS_PORT).host(ACCOUNTS_HOST)
				.path("/account/" + userId + "/email-confirmed");

		boolean body = restTemplate.exchange(builder.build().toUriString(),
				HttpMethod.GET, entity, Boolean.class).getBody();

		return body;
	}

	protected HttpEntity<Object> assembleHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

}
