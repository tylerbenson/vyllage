package editor.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorHandlerController implements ErrorController {

	private final String PATH = "/error";

	//TODO: defaulting to true for now, profiles are not working 
	//@Value("${display.weberror}")
	private boolean displayWebError = true;

	// @RequestMapping(value = PATH, produces = "text/html")
	// @ExceptionHandler(value = { JsonProcessingException.class,
	// ElementNotFoundException.class })
	// public ModelAndView htmlError(HttpServletRequest request,
	// Principal principal, Exception ex) {
	//
	// Map<String, Object> map = new HashMap<>();
	// if (ex.getCause() != null) {
	// map.put("error", ex.getCause().getMessage());
	// } else {
	// map.put("error", ex.getMessage());
	// }
	// return new ModelAndView("error", map);
	// }
	//
	// @RequestMapping(value = PATH, produces = "application/json")
	// @ExceptionHandler(value = { JsonProcessingException.class,
	// ElementNotFoundException.class })
	// public @ResponseBody Map<String, Object> error(HttpServletRequest
	// request,
	// Principal principal, Exception ex) {
	//
	// Map<String, Object> map = new HashMap<>();
	// if (ex.getCause() != null) {
	// map.put("error", ex.getCause().getMessage());
	// } else {
	// map.put("error", ex.getMessage());
	// }
	// return map;
	// }

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
	public ModelAndView errorHtml(HttpServletRequest request) {

		// Authentication currentUser = SecurityContextHolder.getContext()
		// .getAuthentication();
		Map<String, Object> body = getErrorAttributes(request, true);

		body.forEach((k, v) -> System.out.println(k + " " + v));

		body.put("displayWebError", displayWebError);
		return new ModelAndView("error", body);
	}

	@RequestMapping(value = PATH, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, false);
		body.forEach((k, v) -> System.out.println(k + " " + v));

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
