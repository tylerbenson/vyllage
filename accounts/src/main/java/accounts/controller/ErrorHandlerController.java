package accounts.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import user.common.User;
import accounts.service.UserService;

@Controller
public class ErrorHandlerController implements ErrorController {

	private final String PATH = "/error";

	@Value("${display.weberror}")
	private boolean displayWebError;

	@Value("${display.weberror.authority}")
	private String authority;

	@Autowired
	private UserService userService;

	@Override
	public String getErrorPath() {
		return PATH;
	}

	private final ErrorAttributes errorAttributes;

	@Autowired
	public ErrorHandlerController(ErrorAttributes errorAttributes) {
		Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping(value = PATH, produces = "text/html")
	public ModelAndView errorHtml(HttpServletRequest request,
			@AuthenticationPrincipal User currentUser) {

		Map<String, Object> body = getErrorAttributes(request, true);

		if (currentUser != null) {

			if (displayWebError
					&& currentUser != null
					&& currentUser.getAuthorities() != null
					&& currentUser.getAuthorities()
							.stream()
							.anyMatch(
									a -> authority.equalsIgnoreCase(a
											.getAuthority())))
				body.put("displayWebError", true);
			else
				body.put("displayWebError", false);
		} else
			body.put("displayWebError", true);

		return new ModelAndView("error", body);
	}

	@RequestMapping(value = PATH, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, false);

		HttpStatus status = getStatus(request);
		return new ResponseEntity<Map<String, Object>>(body, status);
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request,
			boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(
				request);
		return this.errorAttributes.getErrorAttributes(requestAttributes,
				includeStackTrace);
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		if (statusCode != null) {
			try {
				return HttpStatus.valueOf(statusCode);
			} catch (Exception ex) {
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
