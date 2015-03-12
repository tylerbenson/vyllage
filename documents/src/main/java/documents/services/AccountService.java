package documents.services;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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

import documents.model.AccountNames;

@Service
public class AccountService {

	private final Logger logger = Logger.getLogger(AccountService.class
			.getName());

	// @Autowired
	// private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${accounts.host}")
	public String ACCOUNTS_HOST;

	@Value("${accounts.port}")
	public String ACCOUNTS_PORT;

	public List<AccountNames> getNamesForUsers(List<Long> userIds,
			HttpServletRequest request) {
		Assert.notNull(userIds);
		Assert.notEmpty(userIds);

		HttpHeaders headers = new HttpHeaders();
		/*
		 * TODO There must be a better way to get the right cookie, isn't there?
		 * (This will break if we add more cookies.)
		 */
		headers.set("Cookie",
				"JSESSIONID=" + request.getCookies()[0].getValue());

		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(userIds, headers);

		AccountNames[] body = restTemplate.exchange(
				"http://{host}:" + ACCOUNTS_PORT + "/account/names",
				HttpMethod.POST, entity, AccountNames[].class, ACCOUNTS_HOST)
				.getBody();

		if (body != null)
			return Arrays.asList(body);

		return Arrays.asList();
	}
}
