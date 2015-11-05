package documents.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import documents.services.AccountService;
import documents.services.DocumentService;

@Controller
@RequestMapping("document")
public class DocumentAccessController {

	private DocumentService documentService;
	private AccountService accountService;

	@Inject
	public DocumentAccessController(DocumentService documentService,
			final AccountService accountService) {
		this.documentService = documentService;
		this.accountService = accountService;

	}

	// @RequestMapping(value = "permissions", method = RequestMethod.GET,
	// produces = "application/json")
	// public @ResponseBody List<DocumentAccess> getUserDocumentsPermissions(
	// HttpServletRequest request, @AuthenticationPrincipal User user) {
	//
	// List<DocumentAccess> userDocumentsPermissions = documentService
	// .getUserDocumentsPermissions(user);
	//
	// List<Long> userIds = userDocumentsPermissions.stream()
	// .map(dp -> dp.getUserId()).collect(Collectors.toList());
	//
	// List<AccountContact> contactDataForUsers = accountService
	// .getContactDataForUsers(request, userIds);
	//
	// userDocumentsPermissions.forEach(dp -> {
	// Optional<AccountContact> optionalContactData = contactDataForUsers
	// .stream()
	// .filter(ac -> dp.getUserId().equals(ac.getUserId()))
	// .findFirst();
	//
	// if (optionalContactData.isPresent()) {
	// dp.setUserName(optionalContactData.get().getFirstName() + " "
	// + optionalContactData.get().getLastName());
	//
	// dp.setTagline(optionalContactData.get().getTagline());
	// }
	// });
	//
	// return userDocumentsPermissions;
	// }
}
