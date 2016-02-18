package jobs.services;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.NonNull;

import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service("jobs.DocumentService")
public class DocumentService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentService.class
			.getName());

	private RestTemplate restTemplate;

	@Value("${documents.host:localhost}")
	private final String DOCUMENTS_HOST = "localhost";

	@Value("${documents.port:8080}")
	private final Integer DOCUMENTS_PORT = 8080;

	@Inject
	public DocumentService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getTxtResume(HttpServletRequest request,
			@NonNull Long documentId) {

		HttpEntity<Object> entity = assembleHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/resume/" + documentId + "/file/txt");

		String body = restTemplate.exchange(builder.build().toUriString(),
				HttpMethod.GET, entity, String.class).getBody();

		// I don't want a null string.
		if (!StringUtils.isBlank(body))
			return body;

		return "";
	}

	protected HttpEntity<Object> assembleHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

}
