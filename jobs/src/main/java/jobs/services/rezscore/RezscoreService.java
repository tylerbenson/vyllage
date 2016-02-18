package jobs.services.rezscore;

import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jobs.services.DocumentService;
import jobs.services.rezscore.result.Rezscore;
import jobs.services.rezscore.result.RezscoreResult;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.tools.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.newrelic.api.agent.NewRelic;

@Service
public class RezscoreService {

	private final Logger logger = Logger.getLogger(RezscoreService.class
			.getName());

	private static final int PORT = 80;
	private static final String HOST = "rezscore.com";

	private final RestTemplate restTemplate;

	private final DocumentService documentService;

	// TODO: get key from environment.
	private static final String API_KEY = "75ccf8";

	private RedisCache<String, RezscoreResult> redisCache;

	@Inject
	public RezscoreService(RestTemplate restTemplate,
			RedisCache<String, RezscoreResult> redisCache,
			DocumentService documentService) {
		this.restTemplate = restTemplate;
		this.redisCache = redisCache;
		this.documentService = documentService;

	}

	public Optional<RezscoreResult> getRezscoreAnalysis(
			HttpServletRequest request, Long documentId) {

		String txtResume = documentService.getTxtResume(request, documentId);

		if (StringUtils.isBlank(txtResume))
			return Optional.empty();

		// logger.info(txtResume);

		RezscoreResult rezscoreResult = getRezcoreResult(documentId, txtResume);

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
			rezscoreResult = redisCache.get(
					getKey(documentId),
					() -> {

						RezscoreResult result = getRezscoreAnalysis(resume,
								header, url);

						redisCache.put(getKey(documentId), result);

						return result;
					});

			// changed?
			if (!rezscoreResult.getResume().equals(resume)) {
				rezscoreResult = getRezscoreAnalysis(resume, header, url);

				// update the cache
				redisCache.put(getKey(documentId), rezscoreResult);
			}

		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return rezscoreResult;
	}

	protected RezscoreResult getRezscoreAnalysis(final String resume,
			HttpEntity<String> header, String url) {
		ResponseEntity<Rezscore> responseEntity = restTemplate.exchange(url,
				HttpMethod.GET, header, Rezscore.class);

		Rezscore body = responseEntity.getBody();

		RezscoreResult result = new RezscoreResult(resume, body);
		return result;
	}

	protected String getKey(final Long documentId) {
		return HOST + ":docId:" + documentId;
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
