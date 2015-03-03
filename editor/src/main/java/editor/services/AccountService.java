package editor.services;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

	// @Value("${account.service.url}")
	// public String ACCOUNT_URL;

	public AccountService() {
		// Assert.notNull(environment);
		// ACCOUNT_URL = environment.getProperty("account.service.url");
		// logger.info(ACCOUNT_URL);
	}

	public AccountNames getNamesForUser(Long userId, HttpServletRequest request) {
		Assert.notNull(userId);
		// logger.info("User " + userId);

		// logger.info(restTemplate.getForObject(
		// "http://localhost:8080/account/names/" + userId, String.class));

		// request.getParameterMap().forEach(
		// (k, v) -> logger.info("Header " + k + " " + v));
		//
		// for (Cookie cookie : request.getCookies()) {
		// logger.info(cookie.getValue());
		// }

		// HttpHeaders headers = new HttpHeaders();
		// headers.set("Cookie",
		// "JSESSIONID=" + request.getCookies()[0].getValue());
		//
		// Map<String, String> c = new HashMap<>();
		//
		// HttpEntity<Object> entity = new HttpEntity<Object>(c, headers);
		//
		// HttpEntity names = restTemplate.exchange(
		// "http://localhost:8080/account/names/" + userId,
		// HttpMethod.GET, entity, HttpEntity.class);
		//
		// logger.info(names.toString());

		// return null;

		return restTemplate.getForObject("http://localhost:8080/account/names/"
				+ userId, AccountNames.class);
	}

}
