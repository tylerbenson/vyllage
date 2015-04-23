package accounts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.constants.OrganizationEnum;
import accounts.model.BatchAccount;
import accounts.model.Organization;
import accounts.model.User;
import accounts.model.UserOrganizationRole;
import accounts.model.account.AccountNames;
import accounts.repository.OrganizationRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.UserService;

@Controller
@RequestMapping("admin")
public class AdminUserController {
	@Autowired
	private UserService service;

	@Autowired
	private RoleRepository roleRepository;

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
	public String batchAccountCreation(BatchAccount batch, Model model)
			throws IllegalArgumentException, EmailException {

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

	@RequestMapping(value = "/user/sameOrganization", method = RequestMethod.GET)
	// @PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody boolean sameOrganization(
			@RequestParam Long firstUserId, @RequestParam Long secondUserId)
			throws UserNotFoundException {

		User firstUser = service.getUser(firstUserId);
		User secondUser = service.getUser(secondUserId);
		List<Long> secondUserOrganizationIds = secondUser.getAuthorities()
				.stream()
				.map(uor -> ((UserOrganizationRole) uor).getOrganizationId())
				.collect(Collectors.toList());

		return firstUser
				.getAuthorities()
				.stream()
				.anyMatch(
						uor -> secondUserOrganizationIds
								.contains(((UserOrganizationRole) uor)
										.getOrganizationId()));
	}

	private void prepareBatchError(BatchAccount batch, Model model, String msg) {
		model.addAttribute("organizations", organizationRepository.getAll());
		model.addAttribute("roles", roleRepository.getAll());
		model.addAttribute("batchAccount", batch);
		model.addAttribute("error", msg);
	}

	private void prepareBatch(Model model) {
		List<Organization> allOrganizations;

		if (getUser()
				.getAuthorities()
				.stream()
				.anyMatch(
						uor -> OrganizationEnum.VYLLAGE.getOrganizationId()
								.equals(((UserOrganizationRole) uor)
										.getOrganizationId())))
			allOrganizations = organizationRepository.getAll();
		else
			allOrganizations = organizationRepository.getAll(getUser()
					.getAuthorities()
					.stream()
					.map(uor -> ((UserOrganizationRole) uor)
							.getOrganizationId()).collect(Collectors.toList()));

		model.addAttribute("organizations", allOrganizations);
		// default to the first one.
		model.addAttribute("roles", roleRepository.getAll());

		model.addAttribute("batchAccount", new BatchAccount());
	}

	private User getUser() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		return (User) auth.getPrincipal();
	}

}
