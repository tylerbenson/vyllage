package connections.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import connections.model.AccountContact;
import connections.model.AccountNames;
import connections.model.AdviceRequest;
import connections.model.AdviceRequestParameter;
import connections.model.UserFilterResponse;
import connections.repository.ElementNotFoundException;
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
	public AccountNames accountNames(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		Long userId = user.getUserId();

		List<AccountNames> namesForUsers = accountService.getNamesForUsers(
				request, Arrays.asList(userId));

		if (namesForUsers.isEmpty()) {
			AccountNames an = new AccountNames();
			an.setUserId(userId);
			an.setFirstName("");
			an.setLastName("");
			an.setMiddleName("");
			return an;
		}

		return namesForUsers.get(0);
	}

	@ModelAttribute("userInfo")
	public AccountContact userInfo(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		List<AccountContact> contactDataForUsers = accountService
				.getContactDataForUsers(request,
						Arrays.asList(user.getUserId()));

		if (contactDataForUsers.isEmpty()) {
			return null;
		}
		return contactDataForUsers.get(0);
	}

	@RequestMapping(value = "ask-advice", method = RequestMethod.GET)
	public String askAdvice(HttpServletRequest request,
			@AuthenticationPrincipal User user) {

		if (user.isGuest()) {
			request.getSession().invalidate();
			return "redirect:/";
		}

		return "askAdvice";
	}

	@RequestMapping(value = "ask-advice", method = RequestMethod.POST)
	public String askAdvice(HttpServletRequest request,
			@RequestBody AdviceRequest adviceRequest,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, EmailException {

		validateAdviceRequest(adviceRequest);

		Long userId = user.getUserId();
		String firstName = user.getFirstName();

		Long documentId = adviceService.getUserDocumentId(request, userId);

		// get emails for registered users
		List<AccountContact> emailsFromRegisteredUsers = new ArrayList<>();
		if (!adviceRequest.getUsers().isEmpty())
			emailsFromRegisteredUsers = accountService.getContactDataForUsers(
					request,
					adviceRequest.getUsers().stream().map(u -> u.getUserId())
							.collect(Collectors.toList()));

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

		adviceService.sendRequestAdviceEmail(request, adviceRequestParameters,
				user);

		return "redirect:/resume/" + documentId;
	}

	protected void validateAdviceRequest(AdviceRequest adviceRequest) {
		if (adviceRequest == null
				|| ((adviceRequest.getUsers() == null || adviceRequest
						.getUsers().isEmpty()) && (adviceRequest
						.getNotRegisteredUsers() == null || adviceRequest
						.getNotRegisteredUsers().isEmpty())))
			throw new IllegalArgumentException("No user or email provided.");
	}

	@RequestMapping(value = "users", method = RequestMethod.GET)
	public @ResponseBody UserFilterResponse getUsers(
			HttpServletRequest request,
			@RequestParam(value = "excludeIds", required = false) List<Long> excludeIds,
			@RequestParam(value = "firstNameFilter", required = false) String firstNameFilter,
			@RequestParam(value = "lastNameFilter", required = false) String lastNameFilter,
			@RequestParam(value = "emailFilter", required = false) String emailFilter,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {
		Long userId = user.getUserId();

		// excluding logged in user
		if (excludeIds == null)
			excludeIds = new ArrayList<>();
		excludeIds.add(userId);

		Long documentId = adviceService.getUserDocumentId(request, userId);

		return adviceService.getUsers(request, documentId, userId, excludeIds,
				firstNameFilter, lastNameFilter, emailFilter);
	}

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleIllegalArgumentException(
			Exception ex) {
		Map<String, Object> map = new HashMap<>();
		if (ex.getCause() != null) {
			map.put("error", ex.getCause().getMessage());
		} else {
			map.put("error", ex.getMessage());
		}
		return map;
	}

}
