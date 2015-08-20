package site;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * From http://axelfontaine.com/blog/http-head.html with FilterRegistrationBean
 * to execute before spring security.
 * 
 * @author tyler
 */
public class HttpHeadFilter implements Filter {
	@Configuration
	protected static class HttpHeadFilterConfiguration {
		@Bean
		public FilterRegistrationBean httpHeadFilterRegistrationBean() {
			HttpHeadFilter filter = new HttpHeadFilter();
			FilterRegistrationBean registrationBean = new FilterRegistrationBean();
			registrationBean.setFilter(filter);
			return registrationBean;
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		if (isHttpHead(httpServletRequest)) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			NoBodyResponseWrapper noBodyResponseWrapper = new NoBodyResponseWrapper(
					httpServletResponse);

			chain.doFilter(new ForceGetRequestWrapper(httpServletRequest),
					noBodyResponseWrapper);
			noBodyResponseWrapper.setContentLength();
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

	private boolean isHttpHead(HttpServletRequest request) {
		return "HEAD".equals(request.getMethod());
	}

	private class ForceGetRequestWrapper extends HttpServletRequestWrapper {
		public ForceGetRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getMethod() {
			return "GET";
		}
	}

	private class NoBodyResponseWrapper extends HttpServletResponseWrapper {
		private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream();
		private PrintWriter writer;

		public NoBodyResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return noBodyOutputStream;
		}

		@Override
		public PrintWriter getWriter() throws UnsupportedEncodingException {
			if (writer == null) {
				writer = new PrintWriter(new OutputStreamWriter(
						noBodyOutputStream, getCharacterEncoding()));
			}

			return writer;
		}

		void setContentLength() {
			super.setContentLength(noBodyOutputStream.getContentLength());
		}
	}

	private class NoBodyOutputStream extends ServletOutputStream {
		private int contentLength = 0;

		int getContentLength() {
			return contentLength;
		}

		@Override
		public void write(int b) {
			contentLength++;
		}

		@Override
		public void write(byte buf[], int offset, int len) throws IOException {
			contentLength += len;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener listener) {
		}
	}
}
