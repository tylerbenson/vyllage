package documents.services;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import documents.model.document.sections.DocumentSection;

@Service
public class RezcoreService {

	private static final int PORT = 80;
	private static final String HOST = "rezscore.com";
	private final RestTemplate restTemplate;

	// TODO: get key from environment.
	private static final String API_KEY = "75ccf8";

	@Inject
	public RezcoreService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getRezcoreAnalysis(HttpServletRequest request,
			List<DocumentSection> documentSections) {
		HttpEntity<String> header = assembleHeader(request);
		StringBuilder sb = new StringBuilder();
		String resume = null;

		// sort by position
		documentSections.sort((s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition()));

		// create txt
		for (DocumentSection documentSection : documentSections) {
			sb.append(documentSection.asTxt());
			sb.append("\n");
		}

		resume = sb.toString();

		if (resume == null)
			return "";

		String url = getUrl(resume);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url,
				HttpMethod.GET, header, String.class);

		if (responseEntity == null)
			return "";

		return responseEntity.getBody();
	}

	protected String getUrl(String resume) {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(PORT).host(HOST)
				.path("/a/" + API_KEY + "/grade?resume=" + resume);

		return builder.build().toUriString();
	}

	protected HttpEntity<String> assembleHeader(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		return entity;
	}
}
