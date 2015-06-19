package accounts.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.OrganizationEnum;
import accounts.model.BatchAccount;
import accounts.model.account.AccountContact;
import accounts.model.account.AccountNames;
import accounts.model.form.AccountsRoleManagementForm;
import accounts.model.form.AdminUsersForm;
import accounts.model.form.UserFormObject;
import accounts.model.form.UserOrganizationForm;
import accounts.model.form.UserRoleManagementForm;
import accounts.repository.OrganizationRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.UserService;

@Controller
@RequestMapping("admin")
public class AdminUserController {

	private final UserService userService;

	private final RoleRepository roleRepository;

	private final OrganizationRepository organizationRepository;

	private final AccountSettingsService accountSettingsService;

	@Inject
	public AdminUserController(final UserService userService,
			final RoleRepository roleRepository,
			final OrganizationRepository organizationRepository,
			final AccountSettingsService accountSettingsService) {
		this.userService = userService;
		this.roleRepository = roleRepository;
		this.organizationRepository = organizationRepository;
		this.accountSettingsService = accountSettingsService;
	}

	@ModelAttribute("accountName")
	public AccountNames accountNames(@AuthenticationPrincipal User user) {
		AccountNames name = new AccountNames(user.getUserId(),
				user.getFirstName(), user.getMiddleName(), user.getLastName());
		return name;
	}

	@ModelAttribute("userInfo")
	public AccountContact userInfo(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		List<AccountContact> contactDataForUsers = userService
				.getAccountContactForUsers(accountSettingsService
						.getAccountSettings(Arrays.asList(user.getUserId())));

		if (contactDataForUsers.isEmpty()) {
			return null;
		}
		return contactDataForUsers.get(0);
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String admin(@AuthenticationPrincipal User user, Model model) {
		prepareBatch(model, user);
		return "adminBatchAccountCreation";
	}

	@RequestMapping(value = "/user/role", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showUserRoles(@AuthenticationPrincipal User user, Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);
		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("users", userService
				.getUsersFromOrganization(allOrganizations.get(0)
						.getOrganizationId()));

		model.addAttribute("roles", roleRepository.getAll());
		model.addAttribute("userRoleManagementForm",
				new UserRoleManagementForm());
		return "adminUserRoleManagement";
	}

	@RequestMapping(value = "/user/role", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String setUserRoles(@AuthenticationPrincipal User user,
			UserRoleManagementForm form, Model model) {

		if (form.isInvalid()) {
			List<Organization> allOrganizations = getUserOrganizations(user);
			model.addAttribute("organizations", allOrganizations);
			model.addAttribute("users", userService
					.getUsersFromOrganization(allOrganizations.get(0)
							.getOrganizationId()));

			model.addAttribute("roles", roleRepository.getAll());
			model.addAttribute("userRoleManagementForm", form);

			return "adminUserRoleManagement";
		}

		userService
				.setUserRoles(form
						.getRoles()
						.stream()
						.map(s -> new UserOrganizationRole(form.getUserId(),
								form.getOrganizationId(), s.toUpperCase(), user
										.getUserId()))
						.collect(Collectors.toList()));

		return "redirect:/admin/user/role";
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showUsers(@AuthenticationPrincipal User user, Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);

		List<User> usersFromOrganization = userService
				.getUsersFromOrganization(allOrganizations.get(0)
						.getOrganizationId());

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("users", usersFromOrganization);
		model.addAttribute("form", new AdminUsersForm());

		return "adminUsers";
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showUsersPOST(@AuthenticationPrincipal User user,
			AdminUsersForm form, Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);
		List<User> usersFromOrganization = userService
				.getUsersFromOrganization(form.getOrganizationId());

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("users", usersFromOrganization);

		return "adminUsers";
	}

	@RequestMapping(value = "/users/roles", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminUserRoleManagement(@AuthenticationPrincipal User user,
			Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);
		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("users", userService
				.getUsersFromOrganization(allOrganizations.get(0)
						.getOrganizationId()));

		model.addAttribute("roles", roleRepository.getAll());
		model.addAttribute("accountRolesManagementForm",
				new AccountsRoleManagementForm());
		return "adminAccountRoleManagement";
	}

	@RequestMapping(value = "/users/{userId}/organizations", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminUserOrganizationManagement(
			@AuthenticationPrincipal User user, Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);
		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("form", new UserOrganizationForm());

		return "adminUserOrganizationManagement";
	}

	@RequestMapping(value = "/users/roles", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String setRoles(@AuthenticationPrincipal User user,
			AccountsRoleManagementForm form, Model model) {

		if (form.isInvalid()) {
			List<Organization> allOrganizations = getUserOrganizations(user);
			model.addAttribute("organizations", allOrganizations);
			model.addAttribute("users", userService
					.getUsersFromOrganization(allOrganizations.get(0)
							.getOrganizationId()));

			model.addAttribute("roles", roleRepository.getAll());
			model.addAttribute("accountRolesManagementForm", form);
			System.out.println(form);
			return "adminAccountRoleManagement";
		}

		List<UserOrganizationRole> userOrganizationRoles = new ArrayList<>();

		for (Long userId : form.getUserIds()) {
			for (String role : form.getRoles()) {
				userOrganizationRoles.add(new UserOrganizationRole(userId, form
						.getOrganizationId(), role, user.getUserId()));
			}
		}

		if (form.isAppend())
			userService.appendUserRoles(userOrganizationRoles);
		else
			// replace
			userOrganizationRoles
					.stream()
					.collect(
							Collectors.groupingBy(
									UserOrganizationRole::getUserId,
									Collectors.toList()))
					.forEach((k, v) -> userService.setUserRoles(v));

		return "redirect:/admin/users/roles";
	}

	@RequestMapping(value = "/user/createBatch", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String batchAccountCreation(BatchAccount batch,
			@AuthenticationPrincipal User user, Model model)
			throws IllegalArgumentException, EmailException, IOException {

		if (batch.hasErrors()) {
			prepareBatchError(
					batch,
					model,
					"Please provide ',' or line separated emails and select the Group the users will belong to.",
					user);
			return "adminBatchAccountCreation";
		}

		userService.batchCreateUsers(batch, user);

		prepareBatch(model, user);
		return "adminBatchAccountCreation";
	}

	@RequestMapping(value = "/user/sameOrganization", method = RequestMethod.GET)
	// @PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody boolean sameOrganization(
			@RequestParam Long firstUserId, @RequestParam Long secondUserId,
			@AuthenticationPrincipal User user) throws UserNotFoundException {

		User firstUser = userService.getUser(firstUserId);
		User secondUser = userService.getUser(secondUserId);
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

	@RequestMapping(value = "/organization/{organizationId}/users", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody List<UserFormObject> getUsersFromOrganization(
			@PathVariable Long organizationId) {
		List<UserFormObject> users = new ArrayList<>();

		userService.getUsersFromOrganization(organizationId).forEach(
				u -> users.add(new UserFormObject(u)));

		return users;
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
