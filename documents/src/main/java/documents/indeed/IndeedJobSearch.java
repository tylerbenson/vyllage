package documents.indeed;

import java.util.List;

import javax.inject.Inject;

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

	// params final List<String> queries, final JobType jobType, final String
	// userIp, final String userAgent
	public String searchXML() {
		// example
		final String q = "&q=java";
		final String userIp = "&userip=1.2.3.4";
		final String userAgent = "&useragent=Mozilla/%2F4.0%28Firefox%29";
		final String format = "&format=json";
		final String v = "&v=2";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

		ResponseEntity<String> responseEntity = rest.exchange(url + q + latlong
				+ userIp + userAgent + format + v, HttpMethod.GET, entity,
				String.class);

		Assert.notNull(responseEntity);
		// System.out.println(responseEntity.getBody());
		return responseEntity.getBody();
	}

	public IndeedResponse search() {
		// example
		final String q = "&q=Java";
		final String userIp = "&userip=1.2.3.4";
		final String userAgent = "&useragent=Mozilla/%2F4.0%28Firefox%29";
		final String format = "&format=json";
		final String v = "&v=2";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

		ResponseEntity<IndeedResponse> responseEntity = rest.exchange(url + q
				+ latlong + userIp + userAgent + format + v, HttpMethod.GET,
				entity, IndeedResponse.class);

		Assert.notNull(responseEntity);
		// System.out.println(responseEntity.getBody());
		return responseEntity.getBody();
	}
}
