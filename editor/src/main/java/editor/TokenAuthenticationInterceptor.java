package editor;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * This class is used to add the authentication headers for all rest requests.
 * 
 * @author uh
 *
 */
public class TokenAuthenticationInterceptor implements
		ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {

		@SuppressWarnings("unused")
		HttpHeaders headers = request.getHeaders();
		// headers.add("_csfr", +token);

		return execution.execute(request, body);
	}

}
