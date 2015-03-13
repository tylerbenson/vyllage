package connections.controllers;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import connections.service.AdviceService;
import connections.service.UserFilterResponse;

@Controller
@RequestMapping("resume")
public class AdviceRequestController {

	@Autowired
	private AdviceService service;

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AdviceRequestController.class.getName());

	@RequestMapping(value = "{documentId}/ask-advice", method = RequestMethod.GET)
	public String getResume(@PathVariable final Long documentId) {
		return "requestAdvice";
	}

	@RequestMapping(value = "{documentId}/users", method = RequestMethod.GET)
	public @ResponseBody UserFilterResponse getUsers(
			HttpServletRequest request,
			@PathVariable final Long documentId,
			@RequestParam(value = "excludedUserIds", required = false) final List<Long> excludedUserIds) {
		Long userId = (Long) request.getSession().getAttribute("userId");

		return service.getUsers(request, documentId, userId, excludedUserIds);
	}
}
