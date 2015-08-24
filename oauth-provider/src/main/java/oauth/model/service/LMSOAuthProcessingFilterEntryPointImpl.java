package oauth.model.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class LMSOAuthProcessingFilterEntryPointImpl extends
		OAuthProcessingFilterEntryPoint {

	final static Logger log = LoggerFactory
			.getLogger(LMSOAuthProcessingFilterEntryPointImpl.class);

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		super.commence(request, response, authException);

	}
}
