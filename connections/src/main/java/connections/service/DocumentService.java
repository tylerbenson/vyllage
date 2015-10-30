package connections.service;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import user.common.web.NotifyFeedbackRequest;
import connections.repository.ElementNotFoundException;

@Service("connections.DocumentService")
public class DocumentService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentService.class
			.getName());

	@Value("${documents.host:localhost}")
	private final String DOCUMENTS_HOST = null;

	@Value("${documents.port:8080}")
	private final Integer DOCUMENTS_PORT = null;

	private final RestTemplate restTemplate;

	@Inject
	public DocumentService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Returns the user document Id.
	 *
	 * @param request
	 * @param userId
	 * @return user document id
	 * @throws ElementNotFoundException
	 */
	public Long getUserDocumentId(HttpServletRequest request, Long userId)
			throws ElementNotFoundException {

		HttpEntity<Object> entity = createHeader(request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/document/user").queryParam("userId", userId);

		@SuppressWarnings("unchecked")
		List<Integer> responseBody = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				List.class).getBody();

		if (responseBody.isEmpty())
			throw new ElementNotFoundException(
					"No documents found for user with id '" + userId + "'");

		// TODO: currently the user only has one document, this might change in
		// the future but for now we return the first document id he has
		return new Long(responseBody.get(0));
	}

	public void notifyFeedbackRequest(HttpServletRequest request,
			final Long requestingUserId, final Long userIdtoNotify,
			final Long requestingUserDocumentId) {

		NotifyFeedbackRequest feedbackRequest = new NotifyFeedbackRequest(
				userIdtoNotify, requestingUserDocumentId, requestingUserId);

		HttpEntity<Object> entity = createHeader(feedbackRequest, request);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/notification/request-feedback");

		restTemplate.exchange(builder.build().toUriString(), HttpMethod.POST,
				entity, Void.class);

	}

	protected HttpEntity<Object> createHeader(Object object,
			HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-CSRF-TOKEN", token.getToken());
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(object, headers);
		return entity;
	}

	protected HttpEntity<Object> createHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
		return entity;
	}

}
