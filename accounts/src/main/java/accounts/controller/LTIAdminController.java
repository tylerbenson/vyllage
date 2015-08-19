package accounts.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import oauth.repository.LTIKey;
import oauth.repository.LTIKeyRepository;

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
import accounts.model.form.LTIKeyForm;
import accounts.repository.OrganizationRepository;
import accounts.service.utilities.RandomPasswordGenerator;

@Controller
public class LTIAdminController {

	private final Logger logger = Logger.getLogger(AdminUserController.class
			.getName());

	private static final String NON_ALPHANUMERIC = "[^A-Za-z0-9]";

	private final OrganizationRepository organizationRepository;
	private final LTIKeyRepository LTIKeyRepository;
	private final RandomPasswordGenerator passwordGenerator;

	@Inject
	public LTIAdminController(
			final OrganizationRepository organizationRepository,
			final LTIKeyRepository LTIKeyRepository,
			final RandomPasswordGenerator passwordGenerator) {
		this.organizationRepository = organizationRepository;
		this.LTIKeyRepository = LTIKeyRepository;
		this.passwordGenerator = passwordGenerator;
	}

	@RequestMapping(value = "/admin/lti/keys", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String LTIKeys(@AuthenticationPrincipal User user, Model model) {

		List<Organization> allOrganizations = getAdminOrganizations(user);
		LTIKeyForm LTIKeyForm = new LTIKeyForm();

		Assert.notNull(allOrganizations);
		Assert.notEmpty(allOrganizations);

		LTIKeyForm.setSecret(passwordGenerator.getRandomPassword());

		model.addAttribute("organizations", allOrganizations);
		model.addAttribute("ltiKeyForm", LTIKeyForm);

		return "adminLTIKey";
	}

	@RequestMapping(value = "/admin/lti/keys", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String LTIKeysPost(@AuthenticationPrincipal User user,
			LTIKeyForm LTIKeyForm, Model model) {

		// validate
		if (LTIKeyForm.isInvalid()) {

			List<Organization> allOrganizations = getAdminOrganizations(user);
			Assert.notNull(allOrganizations);
			Assert.notEmpty(allOrganizations);

			if (LTIKeyForm.getSecret() == null
					|| LTIKeyForm.getSecret().isEmpty())
				LTIKeyForm.setSecret(passwordGenerator.getRandomPassword());

			model.addAttribute("organizations", allOrganizations);
			model.addAttribute("ltiKeyForm", LTIKeyForm);

			return "adminLTIKey";
		}

		// to show the result
		final Organization organization = organizationRepository.get(LTIKeyForm
				.getOrganizationId());

		final String consumerKey = organization.getOrganizationName()
				.replaceAll(NON_ALPHANUMERIC, "")
				+ LTIKeyForm.getConsumerKey().replaceAll(NON_ALPHANUMERIC, "");

		// save

		LTIKey key = LTIKeyRepository.save(user, organization, consumerKey,
				LTIKeyForm.getSecret());

		model.addAttribute("organization", organization.getOrganizationName());
		model.addAttribute("consumerKey", consumerKey);
		model.addAttribute("secret", LTIKeyForm.getSecret());

		logger.info(organization.getOrganizationName());
		logger.info(key.getKeyKey());
		logger.info(LTIKeyForm.getSecret());

		return "adminLTIKeyDone";
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
