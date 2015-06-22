package accounts.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
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
import accounts.model.form.OrganizationOptionForm;
import accounts.model.form.UserFormObject;
import accounts.model.form.UserOrganizationForm;
import accounts.model.form.UserRoleManagementForm;
import accounts.repository.OrganizationRepository;
import accounts.repository.RoleRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentService;
import accounts.service.UserService;

@Controller
@RequestMapping("admin")
public class AdminUserController {

	private final Logger logger = Logger.getLogger(AdminUserController.class
			.getName());

	private final UserService userService;

	private final RoleRepository roleRepository;

	private final OrganizationRepository organizationRepository;

	private final AccountSettingsService accountSettingsService;

	private final DocumentService documentService;

	@Inject
	public AdminUserController(final UserService userService,
			final RoleRepository roleRepository,
			final OrganizationRepository organizationRepository,
			final AccountSettingsService accountSettingsService,
			final DocumentService documentService) {
		this.userService = userService;
		this.roleRepository = roleRepository;
		this.organizationRepository = organizationRepository;
		this.accountSettingsService = accountSettingsService;
		this.documentService = documentService;
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

	@RequestMapping(value = "/user/roles", method = RequestMethod.GET)
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

	@RequestMapping(value = "/user/roles", method = RequestMethod.POST)
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

		return "redirect:/admin/user/roles";
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showUsers(final HttpServletRequest request,
			@AuthenticationPrincipal User user, Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);

		List<UserFormObject> usersFromOrganization = userService
				.getUsersFromOrganization(
						allOrganizations.get(0).getOrganizationId())
				.stream()
				.parallel()
				.map(u -> new UserFormObject(u))
				.map(uf -> {
					try {
						uf.setDocuments(documentService.getUserDocumentId(
								request, uf.getUserId()));
					} catch (Exception e) {
						// no documents, empty list
						uf.setDocuments(Collections.emptyList());
					}
					return uf;
				}).collect(Collectors.toList());

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("users", usersFromOrganization);
		model.addAttribute("adminUsersForm", new AdminUsersForm());

		return "adminUsers";
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String showUsersPOST(HttpServletRequest request,
			@AuthenticationPrincipal User user, AdminUsersForm form, Model model) {

		List<Organization> allOrganizations = getUserOrganizations(user);

		List<UserFormObject> usersFromOrganization = userService
				.getUsersFromOrganization(form.getOrganizationId())
				.stream()
				.parallel()
				.map(u -> new UserFormObject(u))
				.map(uf -> {
					try {
						uf.setDocuments(documentService.getUserDocumentId(
								request, uf.getUserId()));
					} catch (Exception e) {
						// no documents, empty list
						uf.setDocuments(Collections.emptyList());
					}
					return uf;
				}).collect(Collectors.toList());

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

	@RequestMapping(value = "/users/roles", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminUserSetRoles(@AuthenticationPrincipal User user,
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
			userService.appendUserOrganizationRoles(userOrganizationRoles);
		else
			// replace
			userService.setUserRoles(userOrganizationRoles);

		return "redirect:/admin/users/roles";
	}

	@RequestMapping(value = "/user/{userId}/organizations", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminUserOrganizationManagement(@PathVariable Long userId,
			@AuthenticationPrincipal User user, Model model)
			throws UserNotFoundException {

		List<Organization> allOrganizations = getUserOrganizations(user);
		List<Organization> userOrganizations = getUserOrganizations(userService
				.getUser(userId));

		List<OrganizationOptionForm> organizationOptions = allOrganizations
				.stream()
				.map(org1 -> new OrganizationOptionForm(org1, userOrganizations
						.stream().anyMatch(org2 -> org1.equals(org2))))
				.collect(Collectors.toList());

		model.addAttribute("organizationOptions", organizationOptions);
		model.addAttribute("userOrganizationForm", new UserOrganizationForm());

		return "adminUserOrganizationManagement";
	}

	@RequestMapping(value = "/user/{userId}/organizations", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminUserSetOrganizations(final UserOrganizationForm form,
			@PathVariable Long userId, @AuthenticationPrincipal User user,
			Model model) throws UserNotFoundException {

		List<Organization> userOrganizations = getUserOrganizations(userService
				.getUser(userId));

		if (form.isInvalid()) {

			List<Organization> allOrganizations = getUserOrganizations(user);
			List<OrganizationOptionForm> organizationOptions = allOrganizations
					.stream()
					.map(org1 -> new OrganizationOptionForm(org1,
							userOrganizations.stream().anyMatch(
									org2 -> org1.equals(org2))))
					.collect(Collectors.toList());

			model.addAttribute("organizationOptions", organizationOptions);
			model.addAttribute("userOrganizationForm", form);
			return "adminUserOrganizationManagement";
		}

		User selectedUser = userService.getUser(userId);

		List<UserOrganizationRole> userOrganizationRoles = new ArrayList<>();

		for (Long organizationId : form.getOrganizationIds()) {
			for (GrantedAuthority grantedAuthority : selectedUser
					.getAuthorities()) {
				userOrganizationRoles.add(new UserOrganizationRole(userId,
						organizationId, grantedAuthority.getAuthority(), user
								.getUserId()));
			}
		}

		userService.setUserOrganization(userOrganizationRoles);

		return "redirect:/admin/user/" + form.getUserId() + "/organizations";
	}

	@RequestMapping(value = "/user/batch", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String admin(@AuthenticationPrincipal User user, Model model) {
		prepareBatch(model, user);
		return "adminBatchAccountCreation";
	}

	@RequestMapping(value = "/user/batch/createBatch", method = RequestMethod.POST)
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

	@RequestMapping(value = "/user/{userId}/enable-disable", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody Map<String, Object> enableDisable(
			@PathVariable Long userId, @AuthenticationPrincipal User user) {

		logger.info("toggling status for  userId " + userId);

		if (user.getUserId().equals(userId)) {
			Map<String, Object> response = new HashMap<>();
			response.put("error", "You cannot disable your own user.");
			return response;
		}

		boolean enableDisableUser = userService.enableDisableUser(userId);

		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("result", enableDisableUser);

		return response;
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
