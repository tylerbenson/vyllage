package connections.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import connections.service.AccountNames;
import connections.service.AccountService;
import connections.service.AdviceService;
import connections.service.UserFilterResponse;

@Controller
@RequestMapping("resume")
public class AdviceRequestController {

	@Autowired
	private AdviceService adviceService;

	@Autowired
	private AccountService accountService;

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AdviceRequestController.class.getName());

	@ModelAttribute("accountName")
	public AccountNames accountNames(HttpServletRequest request) {
		Long userId = (Long) request.getSession().getAttribute("userId");

		List<AccountNames> namesForUsers = accountService.getNamesForUsers(
				Arrays.asList(userId), request);
		return namesForUsers.get(0);
	}

	@RequestMapping(value = "{documentId}/ask-advice", method = RequestMethod.GET)
	public String getResume(@PathVariable final Long documentId) {
		return "requestAdvice";
	}

	@RequestMapping(value = "{documentId}/users", method = RequestMethod.GET)
	public @ResponseBody UserFilterResponse getUsers(
			HttpServletRequest request,
			@PathVariable final Long documentId,
			@RequestParam(value = "excludeIds", required = false) List<Long> excludeIds) {
		Long userId = (Long) request.getSession().getAttribute("userId");

		if (excludeIds == null)
			excludeIds = new ArrayList<>();
		excludeIds.add(userId);

		return adviceService.getUsers(request, documentId, userId, excludeIds);
	}

}
