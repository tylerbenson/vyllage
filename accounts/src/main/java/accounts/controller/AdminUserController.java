package accounts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import accounts.constants.OrganizationEnum;
import accounts.model.BatchAccount;
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
	public AccountNames accountNames(@AuthenticationPrincipal User user) {
		AccountNames name = new AccountNames(user.getUserId(),
				user.getFirstName(), user.getMiddleName(), user.getLastName());
		return name;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
	public String admin(@AuthenticationPrincipal User user, Model model) {
		// if (Features.GOOGLE_ANALYTICS.isActive()) {
		prepareBatch(model, user);
		// }
	public String admin(Model model) {
		prepareBatch(model);
		return "adminAccountManagement";
	}

	@RequestMapping(value = "/user/createBatch", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
	public String batchAccountCreation(BatchAccount batch,
			@AuthenticationPrincipal User user, Model model)
			throws IllegalArgumentException, EmailException {

		if (batch.hasErrors()) {
			prepareBatchError(
					batch,
					model,
					"Please provide ',' or line separated emails and select the Group the users will belong to.",
					user);
			return "adminAccountManagement";
		}

		service.batchCreateUsers(batch, user);

		prepareBatch(model, user);
		return "adminAccountManagement";
	}

	@RequestMapping(value = "/user/sameOrganization", method = RequestMethod.GET)
	// @PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody boolean sameOrganization(
			@RequestParam Long firstUserId, @RequestParam Long secondUserId,
			@AuthenticationPrincipal User user) throws UserNotFoundException {

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

	private void prepareBatchError(BatchAccount batch, Model model, String msg,
			User user) {
		List<Organization> allOrganizations = getUserOrganizations(user);

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("roles", roleRepository.getAll());
		model.addAttribute("batchAccount", batch);
		model.addAttribute("error", msg);
	}

	private void prepareBatch(Model model, User user) {
		List<Organization> allOrganizations = getUserOrganizations(user);

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("roles", roleRepository.getAll());
		model.addAttribute("batchAccount", new BatchAccount());
	}

	private List<Organization> getUserOrganizations(User user) {
		List<Organization> allOrganizations;

		if (user.getAuthorities()
				.stream()
				.anyMatch(
						uor -> OrganizationEnum.VYLLAGE.getOrganizationId()
								.equals(((UserOrganizationRole) uor)
										.getOrganizationId())))
			allOrganizations = organizationRepository.getAll();
		else
			allOrganizations = organizationRepository.getAll(user
					.getAuthorities()
					.stream()
					.map(uor -> ((UserOrganizationRole) uor)
							.getOrganizationId()).collect(Collectors.toList()));
		return allOrganizations;
	}

}
