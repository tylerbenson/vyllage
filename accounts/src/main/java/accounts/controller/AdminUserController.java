package accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import accounts.model.BatchAccount;
import accounts.model.User;
import accounts.model.account.AccountNames;
import accounts.repository.OrganizationRepository;
import accounts.service.UserService;

@Controller
@RequestMapping("admin")
public class AdminUserController {
	@Autowired
	private UserService service;

	@Autowired
	private OrganizationRepository organizationRepository;

	@ModelAttribute("accountName")
	public AccountNames accountNames() {
		User user = getUser();
		AccountNames name = new AccountNames(user.getUserId(),
				user.getFirstName(), user.getMiddleName(), user.getLastName());
		return name;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String admin(Model model) {

		prepareBatch(model);
		return "adminAccountManagement";
	}

	@RequestMapping(value = "/user/createBatch", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String batchAccountCreation(BatchAccount batch, Model model) {

		if (batch.hasErrors()) {
			prepareBatchError(
					batch,
					model,
					"Please provide ',' or line separated emails and select the Group the users will belong to.");
			return "adminAccountManagement";
		}

		service.batchCreateUsers(batch);

		prepareBatch(model);
		return "adminAccountManagement";
	}

	private void prepareBatchError(BatchAccount batch, Model model, String msg) {
		model.addAttribute("organizations", organizationRepository.getAll());
		model.addAttribute("batchAccount", batch);
		model.addAttribute("error", msg);
	}

	private void prepareBatch(Model model) {
		model.addAttribute("organizations", organizationRepository.getAll());
		model.addAttribute("batchAccount", new BatchAccount());
	}

	private User getUser() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		return (User) auth.getPrincipal();
	}

}
