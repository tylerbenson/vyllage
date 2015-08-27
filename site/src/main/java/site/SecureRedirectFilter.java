package site;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.catalina.connector.Request;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.newrelic.api.agent.NewRelic;

/**
 * When running in production mode, force all relative redirects to use https.
 *
 * @author tyler
 */
public class SecureRedirectFilter implements Filter {
	private final boolean isProd;

	@Configuration
	protected static class HttpHeadFilterConfiguration {
		@Bean
		public FilterRegistrationBean secureRedirectFilterRegistrationBean(
				Environment env) {
			FilterRegistrationBean registrationBean = new FilterRegistrationBean();
			SecureRedirectFilter filter = new SecureRedirectFilter(
					env.acceptsProfiles("prod"));
			registrationBean.setFilter(filter);
			return registrationBean;
		}
	}

	public SecureRedirectFilter(boolean isProd) {
		this.isProd = isProd;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (isProd) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;

			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			SecureRedirectResponseWrapper wrapper = new SecureRedirectResponseWrapper(
					httpServletResponse);

			chain.doFilter(httpServletRequest, wrapper);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

	private class SecureRedirectResponseWrapper extends
			HttpServletResponseWrapper {
		private final Logger logger = Logger
				.getLogger(SecureRedirectResponseWrapper.class.getName());
		private Field responseField;

		public SecureRedirectResponseWrapper(HttpServletResponse response) {
			super(response);
			try {
				responseField = org.apache.catalina.connector.ResponseFacade.class
						.getDeclaredField("response");
				responseField.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException e) {
				NewRelic.noticeError(e);
				responseField = null;
			}
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			ServletResponse response = getResponse();
			Request request = null;
			if (response instanceof org.apache.catalina.connector.ResponseFacade) {
				try {
					response = (ServletResponse) responseField.get(response);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					NewRelic.noticeError(e);
				}
			}
			if (response instanceof org.apache.catalina.connector.Response) {
				request = ((org.apache.catalina.connector.Response) response)
						.getRequest();
			}
			if (!location.startsWith("/") || request == null) {
				super.sendRedirect(location);
			} else {
				super.sendRedirect("https://" + request.getServerName()
						+ location);
			}
		}
	}
}
