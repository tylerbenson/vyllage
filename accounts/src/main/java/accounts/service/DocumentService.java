package accounts.service;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import accounts.model.CSRFToken;

@Service("accounts.DocumentService")
public class DocumentService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentService.class
			.getName());

	@Autowired
	private RestTemplate restTemplate;

	@Value("${documents.host:localhost}")
	private final String DOCUMENTS_HOST = null;

	@Value("${documents.port:8080}")
	private final Integer DOCUMENTS_PORT = null;

	/**
	 * Deletes documents from several users.
	 * 
	 * @param request
	 * @param token
	 */
	public void deleteUsers(HttpServletRequest request, List<Long> userIds,
			CSRFToken token) {
		assert userIds != null && !userIds.isEmpty();

		HttpEntity<Object> entity = assembleHeader(request, userIds, token);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/document/delete");

		builder.queryParam("userIds", userIds.stream().map(Object::toString)
				.collect(Collectors.joining(",")));

		restTemplate.exchange(builder.build().toUriString(), HttpMethod.DELETE,
				entity, Void.class).getBody();

	}

	protected HttpEntity<Object> assembleHeader(HttpServletRequest request,
			List<Long> userIds, CSRFToken token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.set("X-CSRF-TOKEN", token.getValue());
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(headers);
		return entity;
	}

}
