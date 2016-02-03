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
import documents.services.rezscore.result.Rezscore;
import documents.services.rezscore.result.RezscoreResult;

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

	public Optional<RezscoreResult> getRezscoreAnalysis(
			DocumentHeader documentHeader,
			List<DocumentSection> documentSections) {

		if (documentHeader == null || documentSections == null
				|| documentSections.isEmpty())
			return Optional.empty();

		final Long documentId = getDocumentId(documentSections);

		// sort by position
		sortSections(documentSections);

		// create txt
		final String resume = createTxtResume(documentHeader, documentSections);

		// logger.info(resume);

		RezscoreResult rezscoreResult = getRezcoreResult(documentId, resume);

		return Optional.ofNullable(rezscoreResult);
	}

	/**
	 * Returns Rezscore analysis.
	 * 
	 * @param documentId
	 * @param resume
	 * @return
	 */
	protected RezscoreResult getRezcoreResult(final Long documentId,
			final String resume) {
		HttpEntity<String> header = assembleHeader();

		String url = getUrl(resume);

		RezscoreResult rezscoreResult = null;
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
		return rezscoreResult;
	}

	/**
	 * Creates a plain text resume based on the document header and document
	 * sections.
	 * 
	 * @param documentHeader
	 * @param documentSections
	 * @return plain text resume
	 */
	protected String createTxtResume(DocumentHeader documentHeader,
			List<DocumentSection> documentSections) {

		StringBuilder sb = new StringBuilder();
		sb.append(documentHeader.asTxt()).append("\n");

		for (DocumentSection documentSection : documentSections) {
			sb.append(documentSection.asTxt());
			sb.append("\n");
		}

		final String resume = sb.toString();
		return resume;
	}

	/**
	 * Sort sections by sectionPosition.
	 * 
	 * @param documentSections
	 */
	protected void sortSections(List<DocumentSection> documentSections) {
		documentSections.sort((s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition()));
	}

	/**
	 * Get document id from the sections.
	 * 
	 * @param documentSections
	 * @return
	 */
	protected Long getDocumentId(List<DocumentSection> documentSections) {
		return documentSections.stream().map(ds -> ds.getDocumentId())
				.collect(Collectors.toList()).get(0);
	}

	/**
	 * Creates Rezscore url endpoint.
	 * 
	 * @param resume
	 * @return
	 */
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
