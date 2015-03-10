package connections.service;

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

	public UserFilterResponse getUsers(HttpServletRequest request, Long userId,
			List<Long> excludedIds) {
		UserFilterResponse response = new UserFilterResponse();
		// get recent users from Documents:
		// send my user id
		// receive map<userId, FirstName>
		// add each pair into the filter response

		Assert.notNull(userId);

		HttpEntity<Object> entity = addCookieToHeader(request);

		List<FilteredUser> advisors = getAdvisors(userId, entity);

		Assert.notNull(advisors);

		System.out.println("Received Advisors: ");
		// advisors.forEach(System.out::println);

		response.setRecommended(advisors);

		return response;
	}

	private HttpEntity<Object> addCookieToHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("Cookie",
				"JSESSIONID=" + request.getCookies()[0].getValue());

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

	@SuppressWarnings("unchecked")
	private List<FilteredUser> getAdvisors(Long userId,
			HttpEntity<Object> entity) {
		return restTemplate
				.exchange(
						"http://{host}:" + ACCOUNTS_PORT
								+ "/account/advisors/{userId}", HttpMethod.GET,
						entity, List.class, ACCOUNTS_HOST, userId).getBody();
	}

}
