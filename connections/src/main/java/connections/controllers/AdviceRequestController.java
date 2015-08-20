package connections.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
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
import user.common.web.UserInfo;
import connections.model.AccountNames;
import connections.model.AdviceRequest;
import connections.model.AdviceRequestParameter;
import connections.model.UserFilterResponse;
import connections.repository.ElementNotFoundException;
import connections.service.AdviceService;

@Controller
@RequestMapping("resume")
public class AdviceRequestController {

	private final AdviceService adviceService;

	@Inject
	public AdviceRequestController(AdviceService adviceService) {
		this.adviceService = adviceService;
	}

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AdviceRequestController.class.getName());

	@ModelAttribute("accountName")
	public AccountNames accountNames(@AuthenticationPrincipal User user) {
		AccountNames an = new AccountNames();

		if (user == null) {
			an.setUserId(null);
			an.setFirstName("");
			an.setLastName("");
			an.setMiddleName("");
		} else {
			an.setUserId(user.getUserId());
			an.setFirstName(user.getFirstName());
			an.setLastName(user.getLastName());
			an.setMiddleName(user.getMiddleName());
		}
		return an;
	}

	@ModelAttribute("userInfo")
	public UserInfo userInfo(@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		return new UserInfo(user);
	}

	@RequestMapping(value = "get-feedback", method = RequestMethod.GET)
	public String askAdvice(HttpServletRequest request,
			@AuthenticationPrincipal User user) {

		return "getFeedback";
	}

	@RequestMapping(value = "get-feedback", method = RequestMethod.POST)
	public String askAdvice(HttpServletRequest request,
			@RequestBody AdviceRequest adviceRequest,
			@AuthenticationPrincipal User user) throws EmailException,
			ElementNotFoundException {

		validateAdviceRequest(adviceRequest);

		Long userId = user.getUserId();
		String firstName = user.getFirstName();

		Long documentId = adviceService.getUserDocumentId(request, userId);

		AdviceRequestParameter adviceRequestParameters = new AdviceRequestParameter();
		adviceRequestParameters.setDocumentId(documentId);
		adviceRequestParameters.setRegisteredUsersContactData(adviceRequest
				.getUsers());
		adviceRequestParameters.setNotRegisteredUsers(adviceRequest
				.getNotRegisteredUsers());
		adviceRequestParameters.setSenderName(firstName);
		adviceRequestParameters.setSubject(adviceRequest.getSubject());
		adviceRequestParameters.setMessage(adviceRequest.getMessage());
		adviceRequestParameters.setAllowGuestComments(adviceRequest
				.getAllowGuestComments());

		adviceService.sendRequestAdviceEmail(request, adviceRequestParameters,
				user);

		return "redirect:/resume/" + documentId;
	}

	protected void validateAdviceRequest(AdviceRequest adviceRequest) {

		boolean usersIsNullOrEmpty = adviceRequest != null
				&& (adviceRequest.getUsers() == null || adviceRequest
						.getUsers().isEmpty());

		boolean notRegisteredUsersIsNullOrEmpty = adviceRequest != null
				&& (adviceRequest.getNotRegisteredUsers() == null || adviceRequest
						.getNotRegisteredUsers().isEmpty());

		if (adviceRequest == null
				|| (usersIsNullOrEmpty && notRegisteredUsersIsNullOrEmpty))
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
