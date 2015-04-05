package connections.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AdviceService {

	@Autowired
	private RestTemplate restTemplate;

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

}
