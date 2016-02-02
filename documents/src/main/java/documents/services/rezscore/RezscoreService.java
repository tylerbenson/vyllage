package documents.services.rezscore;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.services.AccountService;

@Service
public class RezscoreService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AccountService.class
			.getName());

	private static final int PORT = 80;
	private static final String HOST = "rezscore.com";
	private final RestTemplate restTemplate;

	// TODO: get key from environment.
	private static final String API_KEY = "75ccf8";

	@Inject
	public RezscoreService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getRezcoreAnalysis(DocumentHeader documentHeader,
			List<DocumentSection> documentSections) {
		HttpEntity<String> header = assembleHeader();
		StringBuilder sb = new StringBuilder();
		String resume = null;

		// sort by position
		documentSections.sort((s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition()));

		// create txt
		sb.append(documentHeader.asTxt()).append("\n");

		for (DocumentSection documentSection : documentSections) {
			sb.append(documentSection.asTxt());
			sb.append("\n");
		}

		resume = sb.toString();

		// logger.info(resume);

		String url = getUrl(resume);

		ResponseEntity<Rezscore> responseEntity = restTemplate.exchange(url,
				HttpMethod.GET, header, Rezscore.class);

		if (responseEntity == null)
			return "";

		return responseEntity.getBody().toString();
	}

	protected String getUrl(String resume) {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		builder.scheme("http").port(PORT).host(HOST)
				.path("/a/" + API_KEY + "/grade?resume=" + resume);

		return builder.build().toUriString();
	}

	protected HttpEntity<String> assembleHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		return entity;
	}
}
