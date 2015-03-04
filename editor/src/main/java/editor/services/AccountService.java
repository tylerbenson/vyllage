package editor.services;

import java.util.logging.Logger;

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

	public AccountNames getNamesForUser(Long userId, HttpServletRequest request) {
		Assert.notNull(userId);
		// logger.info("User " + userId);

		// request.getParameterMap().forEach(
		// (k, v) -> logger.info("Header " + k + " " + v));

		// for (Cookie cookie : request.getCookies()) {
		// logger.info(cookie.getValue());
		// }

		HttpHeaders headers = new HttpHeaders();
		/*
		 * TODO There must be a better way to get the right cookie, isn't there?
		 * (This will break if we add more cookies.)
		 */
		headers.set("Cookie",
				"JSESSIONID=" + request.getCookies()[0].getValue());

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

		// Sadly it doesn't like port past in as a variable...
		return restTemplate.exchange(
				"http://{host}:" + ACCOUNTS_PORT + "/account/names/{userId}",
				HttpMethod.GET, entity, AccountNames.class, ACCOUNTS_HOST,
				userId).getBody();
	}
}
