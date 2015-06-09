package site;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.newrelic.api.agent.NewRelic;

@Component
public class UserAgentRequestInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		addIPAddress(request);

		NewRelic.addCustomParameter("UserAgent",
				request.getHeader("User-Agent"));
		return true;
	}

	public void addIPAddress(HttpServletRequest request) {
		// if the user is behind a proxy, etc
		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null)
			ipAddress = request.getRemoteAddr();

		NewRelic.addCustomParameter("userIP", ipAddress);
	}
}
