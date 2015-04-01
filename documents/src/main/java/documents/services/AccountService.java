package documents.services;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import documents.model.AccountContact;
import documents.model.AccountNames;

@Service
public class AccountService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AccountService.class
			.getName());

	@Autowired
	private RestTemplate restTemplate;

	@Value("${accounts.host:localhost}")
	private final String ACCOUNTS_HOST = null;

	@Value("${accounts.port:8080}")
	private final Integer ACCOUNTS_PORT = null;

	public List<AccountNames> getNamesForUsers(List<Long> userIds,
			HttpServletRequest request) {
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

	public List<AccountContact> getContactDataForUsers(List<Long> userIds,
			HttpServletRequest request) {
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

	protected HttpEntity<Object> assembleHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		/*
		 * TODO There must be a better way to get the right cookie, isn't there?
		 * (This will break if we add more cookies.)
		 */
		headers.set("Cookie",
				"JSESSIONID=" + request.getCookies()[0].getValue());

		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

}
