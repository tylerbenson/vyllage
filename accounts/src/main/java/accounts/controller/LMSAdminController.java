package accounts.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import oauth.repository.LMSKey;
import oauth.repository.LMSKeyRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.model.form.LMSKeyForm;
import accounts.repository.OrganizationRepository;
import accounts.service.utilities.RandomPasswordGenerator;

@Controller
public class LMSAdminController {

	private final Logger logger = Logger.getLogger(AdminUserController.class
			.getName());

	private static final String NON_ALPHANUMERIC = "^[^a-zA-Z0-9]+|[^a-zA-Z0-9\\s]+$";

	private final OrganizationRepository organizationRepository;
	private final LMSKeyRepository lMSKeyRepository;
	private final RandomPasswordGenerator passwordGenerator;

	@Inject
	public LMSAdminController(
			final OrganizationRepository organizationRepository,
			LMSKeyRepository lMSKeyRepository,
			final RandomPasswordGenerator passwordGenerator) {
		this.organizationRepository = organizationRepository;
		this.lMSKeyRepository = lMSKeyRepository;
		this.passwordGenerator = passwordGenerator;
	}

	@RequestMapping(value = "/admin/lms/keys", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String lmsKeys(@AuthenticationPrincipal User user, Model model) {

		List<Organization> allOrganizations = getAdminOrganizations(user);
		LMSKeyForm lmsKeyForm = new LMSKeyForm();

		Assert.notNull(allOrganizations);
		Assert.notEmpty(allOrganizations);

		lmsKeyForm.setSecret(passwordGenerator.getRandomPassword());

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("lmsKeyForm", lmsKeyForm);

		return "adminLMSKey";
	}

	@RequestMapping(value = "/admin/lms/keys", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String lmsKeysPost(@AuthenticationPrincipal User user,
			LMSKeyForm lmsKeyForm, Model model) {

		// validate
		if (lmsKeyForm.isInvalid()) {

			List<Organization> allOrganizations = getAdminOrganizations(user);
			Assert.notNull(allOrganizations);
			Assert.notEmpty(allOrganizations);

			lmsKeyForm.setSecret(passwordGenerator.getRandomPassword());

			model.addAttribute("organizations", allOrganizations);
			model.addAttribute("lmsKeyForm", lmsKeyForm);

			return "adminLMSKey";
		}

		// to show the result
		final Organization organization = organizationRepository.get(lmsKeyForm
				.getOrganizationId());

		final String consumerKey = organization.getOrganizationName()
				.replaceAll(NON_ALPHANUMERIC, "")
				+ lmsKeyForm.getConsumerKey().replaceAll(NON_ALPHANUMERIC, "");

		// save

		LMSKey key = lMSKeyRepository.save(user, organization, consumerKey,
				lmsKeyForm.getSecret());

		model.addAttribute("organization", organization.getOrganizationName());
		model.addAttribute("consumerKey", consumerKey);
		model.addAttribute("secret", lmsKeyForm.getSecret());

		logger.info(organization.getOrganizationName());
		logger.info(key.getKeyKey());
		logger.info(lmsKeyForm.getSecret() + " hash: " + key.getSecret());

		return "adminLMSKeyDone";
	}

	/**
	 * Returns the user's organizations where the user is admin. If the user is
	 * Vyllage admin then he sees all organizations.
	 * 
	 * @param user
	 * @return
	 */
	private List<Organization> getAdminOrganizations(User user) {
		List<Organization> allOrganizations;

		if (user.isVyllageAdmin())
			allOrganizations = organizationRepository.getAll();
		else
			allOrganizations = organizationRepository.getAll(user
					.getAuthorities()
					.stream()
					.filter(uor -> RolesEnum.ADMIN.name().equalsIgnoreCase(
							((UserOrganizationRole) uor).getAuthority()))
					.map(uor -> ((UserOrganizationRole) uor)
							.getOrganizationId()).collect(Collectors.toList()));
		return allOrganizations;
	}
}
