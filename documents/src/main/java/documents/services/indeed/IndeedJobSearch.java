package documents.services.indeed;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

@Service
public class IndeedJobSearch {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndeedJobSearch.class
			.getName());

	private static final String key = "1758907727091006";

	private static final String url = "http://api.indeed.com/ads/apisearch?publisher="
			+ key;

	private final String latlong = "&latlong=1";

	private final RestTemplate rest;

	@Inject
	public IndeedJobSearch(RestTemplate restTemplate) {

		List<HttpMessageConverter<?>> converters = restTemplate
				.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
				jsonConverter.setObjectMapper(new ObjectMapper());
				jsonConverter
						.setSupportedMediaTypes(ImmutableList
								.of(new MediaType(
										"application",
										"json",
										MappingJackson2HttpMessageConverter.DEFAULT_CHARSET),
										new MediaType(
												"application",
												"javascript",
												MappingJackson2HttpMessageConverter.DEFAULT_CHARSET),
										new MediaType(
												"text",
												"xml",
												MappingJackson2HttpMessageConverter.DEFAULT_CHARSET)));
			}
		}
		this.rest = restTemplate;
	}

	public IndeedResponse search(@NonNull final List<String> queries,
			@NonNull final String userIp, @NonNull final String userAgent,
			long start) {
		// final JobType jobType to add if there's a way to specify from the
		// frontend, this is not defined yet.

		Assert.notEmpty(queries);

		final String query = "&q="
				+ queries.stream().collect(Collectors.joining(" OR "));

		final String sentUserIp = StringUtils.isBlank(userIp) ? "&userip=1.2.3.4"
				: "&userip=" + userIp;
		final String sentUserAgent = StringUtils.isBlank(userAgent) ? "&useragent=Mozilla/%2F4.0%28Firefox%29"
				: "&useragent=" + userAgent;

		final String startAt = "&start=" + start;
		final String format = "&format=json";
		final String v = "&v=2";

		// final String jobTypeString = "&jt=" + jobType.name();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

		String finalUrl = url + query + latlong + sentUserIp + sentUserAgent
				+ startAt + format + "&psf=advsrch" + v;

		// logger.info(finalUrl);

		ResponseEntity<IndeedResponse> responseEntity = rest.exchange(finalUrl,
				HttpMethod.GET, entity, IndeedResponse.class);

		Assert.notNull(responseEntity);
		// System.out.println(responseEntity.getBody());
		return responseEntity.getBody();
	}

	public String searchXML() {
		// example
		final String q = "&q=java";
		final String userIp = "&userip=1.2.3.4";
		final String userAgent = "&useragent=Mozilla/%2F4.0%28Firefox%29";
		final String format = "&format=json";
		final String v = "&v=2";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

		String finalUrl = url + q + latlong + userIp + userAgent + format + v;
		// logger.info(finalUrl);

		ResponseEntity<String> responseEntity = rest.exchange(finalUrl,
				HttpMethod.GET, entity, String.class);

		Assert.notNull(responseEntity);
		// System.out.println(responseEntity.getBody());
		return responseEntity.getBody();
	}

	public IndeedResponse searchJson() {
		// example
		final String q = "&q=Java";
		final String userIp = "&userip=1.2.3.4";
		final String userAgent = "&useragent=Mozilla/%2F4.0%28Firefox%29";
		final String format = "&format=json";
		final String v = "&v=2";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

		String finalUrl = url + q + latlong + userIp + userAgent + format + v;
		// logger.info(finalUrl);

		ResponseEntity<IndeedResponse> responseEntity = rest.exchange(finalUrl,
				HttpMethod.GET, entity, IndeedResponse.class);

		Assert.notNull(responseEntity);
		// System.out.println(responseEntity.getBody());
		return responseEntity.getBody();
	}

}
