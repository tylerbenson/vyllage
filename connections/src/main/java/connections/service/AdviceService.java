package connections.service;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class AdviceService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${accounts.host}")
	public String ACCOUNTS_HOST;

	@Value("${accounts.port}")
	public String ACCOUNTS_PORT;

	@Value("${documents.host}")
	public String DOCUMENTS_HOST;

	@Value("${documents.port}")
	public String DOCUMENTS_PORT;

	public UserFilterResponse getUsers(HttpServletRequest request,
			Long documentId, Long userId, List<Long> excludedIds) {
		UserFilterResponse response = new UserFilterResponse();
		// get recent users from Documents:
		// send my user id
		// receive map<userId, FirstName>
		// add each pair into the filter response

		Assert.notNull(userId);

		HttpEntity<Object> entity = addCookieToHeader(request);

		List<AccountNames> advisors = getAdvisors(userId, entity);
		Assert.notNull(advisors);

		response.setRecommended(advisors);

		List<AccountNames> recentUsers = getRecentUsers(documentId, entity);
		Assert.notNull(recentUsers);

		response.setRecent(recentUsers);

		return response;
	}

	protected HttpEntity<Object> addCookieToHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("Cookie",
				"JSESSIONID=" + request.getCookies()[0].getValue());

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

	protected List<AccountNames> getRecentUsers(Long documentId,
			HttpEntity<Object> entity) {

		AccountNames[] body = restTemplate.exchange(
				"http://{host}:" + DOCUMENTS_PORT
						+ "/resume/{documentId}/recentUsers", HttpMethod.GET,
				entity, AccountNames[].class, DOCUMENTS_HOST, documentId)
				.getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

	protected List<AccountNames> getAdvisors(Long userId,
			HttpEntity<Object> entity) {
		AccountNames[] body = restTemplate
				.exchange(
						"http://{host}:" + ACCOUNTS_PORT
								+ "/account/advisors/{userId}", HttpMethod.GET,
						entity, AccountNames[].class, ACCOUNTS_HOST, userId)
				.getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}

}
