package site.service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service("site.DocumentService")
public class DocumentService {
	@Value("${documents.host:localhost}")
	private final String DOCUMENTS_HOST = null;

	@Value("${documents.port:8080}")
	private final Integer DOCUMENTS_PORT = null;

	private final RestTemplate restTemplate;

	@Inject
	public DocumentService(final RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Returns the user document Id.
	 *
	 * @param request
	 * @param userId
	 * @return date in unix format of document last modification date
	 */
	public Long getUserDocumentLastModification(
			@NonNull final HttpServletRequest request,
			@NonNull final Long userId) {

		HttpEntity<Object> entity = createHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/document/user/" + userId + "/modified-date");

		Long responseBody = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				Long.class).getBody();

		return responseBody;
	}

	protected HttpEntity<Object> createHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}
}
