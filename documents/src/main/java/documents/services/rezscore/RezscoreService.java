package documents.services.rezscore;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.newrelic.api.agent.NewRelic;

import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.services.AccountService;

@Service
public class RezscoreService {

	private final Logger logger = Logger.getLogger(AccountService.class
			.getName());

	private static final int PORT = 80;
	private static final String HOST = "rezscore.com";
	private final RestTemplate restTemplate;

	// TODO: get key from environment.
	private static final String API_KEY = "75ccf8";

	private final Cache<Long, RezscoreResult> results;

	@Inject
	public RezscoreService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;

		results = CacheBuilder.newBuilder().maximumSize(10)
				.expireAfterAccess(15, TimeUnit.MINUTES).build();
	}

	public Optional<RezscoreResult> getRezcoreAnalysis(
			DocumentHeader documentHeader,
			List<DocumentSection> documentSections) {
		HttpEntity<String> header = assembleHeader();
		StringBuilder sb = new StringBuilder();

		if (documentHeader == null || documentSections == null
				|| documentSections.isEmpty())
			return Optional.empty();

		final Long documentId = documentSections.stream()
				.map(ds -> ds.getDocumentId()).collect(Collectors.toList())
				.get(0);

		// sort by position
		documentSections.sort((s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition()));

		// create txt
		sb.append(documentHeader.asTxt()).append("\n");

		for (DocumentSection documentSection : documentSections) {
			sb.append(documentSection.asTxt());
			sb.append("\n");
		}

		final String resume = sb.toString();

		// logger.info(resume);

		String url = getUrl(resume);

		RezscoreResult rezscoreResult = null;

		// get from cache
		try {
			rezscoreResult = results
					.get(documentId,
							() -> {

								ResponseEntity<Rezscore> responseEntity = restTemplate
										.exchange(url, HttpMethod.GET, header,
												Rezscore.class);

								Rezscore body = responseEntity.getBody();
								RezscoreResult result = new RezscoreResult(
										resume, body);
								results.put(documentId, result);

								return result;
							});
		} catch (RestClientException | ExecutionException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return Optional.ofNullable(rezscoreResult);
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
