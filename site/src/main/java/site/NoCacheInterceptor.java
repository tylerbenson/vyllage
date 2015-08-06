package site;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class NoCacheInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		response.setHeader("Cache-Control", "no-cache,no-store");

		// seems like these are not required

		// response.setHeader("Pragma", "No-cache");
		// response.setHeader("Cache-Control",
		// "no-cache,no-store,must-revalidate");
		// response.setDateHeader("Expires", 0);
		return true;
	};
}
