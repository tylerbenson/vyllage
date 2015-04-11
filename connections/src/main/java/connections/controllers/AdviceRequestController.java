package connections.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import connections.model.AccountContact;
import connections.model.AccountNames;
import connections.model.AdviceRequest;
import connections.model.AdviceRequestParameter;
import connections.model.UserFilterResponse;
import connections.service.AccountService;
import connections.service.AdviceService;

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
				request, Arrays.asList(userId));
		return namesForUsers.get(0);
	}

	@RequestMapping(value = "{documentId}/ask-advice", method = RequestMethod.GET)
	public String askAdvice(@PathVariable final Long documentId) {
		return "askAdvice";
	}

	@RequestMapping(value = "{documentId}/ask-advice", method = RequestMethod.POST)
	public String askAdvice(HttpServletRequest request,
			@PathVariable final Long documentId,
			@RequestBody AdviceRequest adviceRequest) throws EmailException {

		String firstName = (String) request.getSession().getAttribute(
				"userFirstName");

		// get emails for registered users
		List<AccountContact> emailsFromRegisteredUsers = accountService
				.getContactDataForUsers(
						request,
						adviceRequest.getUsers().stream()
								.map(u -> u.getUserId())
								.collect(Collectors.toList()));

		System.out.println("registered emails " + emailsFromRegisteredUsers);

		AdviceRequestParameter adviceRequestParameters = new AdviceRequestParameter();
		adviceRequestParameters.setDocumentId(documentId);
		adviceRequestParameters.setCSRFToken(adviceRequest.getCSRFToken());
		adviceRequestParameters
				.setRegisteredUsersContactData(emailsFromRegisteredUsers);
		adviceRequestParameters.setNotRegisteredUsers(adviceRequest
				.getNotRegisteredUsers());
		adviceRequestParameters.setSenderName(firstName);
		adviceRequestParameters.setSubject(adviceRequest.getSubject());
		adviceRequestParameters.setMessage(adviceRequest.getMessage());
		adviceService.sendRequestAdviceEmail(request, adviceRequestParameters);

		return "redirect:/resume/" + documentId;
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
