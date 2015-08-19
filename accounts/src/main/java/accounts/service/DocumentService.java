package accounts.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import accounts.model.account.settings.DocumentAccess;
import accounts.repository.ElementNotFoundException;

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
	public void deleteUsers(HttpServletRequest request, List<Long> userIds) {
		assert userIds != null && !userIds.isEmpty();

		HttpEntity<Object> entity = createHeader(request, null);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/document/delete");

		builder.queryParam("userIds", userIds.stream().map(Object::toString)
				.collect(Collectors.joining(",")));

		restTemplate.exchange(builder.build().toUriString(), HttpMethod.DELETE,
				entity, Void.class).getBody();

	}

	/**
	 * Returns the user document Id.
	 * 
	 * @param request
	 * 
	 * @param userId
	 * @return
	 * @throws ElementNotFoundException
	 */
	public List<Long> getUserDocumentId(HttpServletRequest request, Long userId)
			throws ElementNotFoundException {

		HttpEntity<Object> entity = createHeader(request, null);

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

		return responseBody.stream().map(i -> i.longValue())
				.collect(Collectors.toList());
	}

	public List<DocumentAccess> getUserDocumentsAccess(
			HttpServletRequest request) {
		HttpEntity<Object> entity = createHeader(request, null);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(DOCUMENTS_PORT).host(DOCUMENTS_HOST)
				.path("/document/permissions");

		// List can't be used because exchange returns a list of
		// linkedHashMaps...

		DocumentAccess[] responseBody = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				DocumentAccess[].class).getBody();

		if (responseBody != null)
			return Arrays.asList(responseBody);
		return Collections.emptyList();
	}

	public Map<String, String> getDocumentHeaderTagline(
			HttpServletRequest request, List<Long> userIds) {
		assert userIds != null && !userIds.isEmpty();

		HttpEntity<Object> entity = createHeader(request, null);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http")
				.port(DOCUMENTS_PORT)
				.host(DOCUMENTS_HOST)
				.path("/resume/header/taglines")
				.queryParam(
						"userIds",
						userIds.stream().map(Object::toString)
								.collect(Collectors.joining(",")));

		@SuppressWarnings("unchecked")
		// RestTemplate returns the key of the map as String.
		Map<String, String> response = restTemplate.exchange(
				builder.build().toUriString(), HttpMethod.GET, entity,
				HashMap.class).getBody();

		return response;
	}

	// unfortunately POST from Accounts to Documents after a programatic login
	// doesn't work.
	// /**
	// * Creates document permissions for the given document and user.
	// *
	// * @param doclink
	// * @param request
	// */
	// public void createDocumentPermission(HttpServletRequest request,
	// AbstractDocumentLink doclink) {
	// LinkPermissions lp = new LinkPermissions();
	// lp.setAllowGuestComments(doclink.getAllowGuestComments());
	// lp.setDocumentId(doclink.getDocumentId());
	// lp.setUserId(doclink.getUserId());
	//
	// HttpEntity<Object> entity = createHeader(request, lp);
	//
	// // HttpHeaders headers = new HttpHeaders();
	// // headers.set("Cookie", request.getHeader("Cookie"));
	// // headers.setContentType(MediaType.APPLICATION_JSON);
	// //
	// // HttpEntity<LinkPermissions> entity = new HttpEntity<LinkPermissions>(
	// // lp, headers);
	//
	// UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
	//
	// builder.scheme("http")
	// .port(DOCUMENTS_PORT)
	// .host(DOCUMENTS_HOST)
	// .path("/document/" + doclink.getDocumentId()
	// + "/permissions/user/" + doclink.getUserId());
	//
	// restTemplate.exchange(builder.build().toUriString(), HttpMethod.POST,
	// entity, Void.class);
	//
	// }

	protected HttpEntity<Object> createHeader(HttpServletRequest request,
			Object object) {
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", request.getHeader("Cookie"));
		if (token != null) {
			headers.set("X-CSRF-TOKEN", token.getToken());
			// headers.set("X-CSRF-HEADER", token.getHeaderName());
			// headers.set("X-CSRF-PARAM", token.getParameterName());
		}
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<Object>(object, headers);
		return entity;
	}

}
