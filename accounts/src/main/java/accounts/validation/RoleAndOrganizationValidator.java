package accounts.validation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import accounts.model.Organization;
import accounts.model.Role;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.OrganizationRepository;
import accounts.repository.OrganizationRoleRepository;
import accounts.repository.RoleRepository;
import accounts.service.UserService;

public class RoleAndOrganizationValidator {

	@Autowired
	private UserService service;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private OrganizationRoleRepository organizationRoleRepository;

	public void validate(List<AccountSetting> collect) {
		assert collect.size() == 2;

		Optional<AccountSetting> roleSetting = collect.stream()
				.filter(s -> s.getName().equalsIgnoreCase("role")).findFirst();
		Optional<AccountSetting> orgSetting = collect.stream()
				.filter(s -> s.getName().equalsIgnoreCase("organization"))
				.findFirst();

		assert roleSetting.isPresent() && orgSetting.isPresent();

		Role role = roleRepository.get(roleSetting.get().getValue());

		if (role == null)
			setErrorMessage(roleSetting.get(), "Role '"
					+ roleSetting.get().getValue() + "' not found.");

		Organization orgByName = organizationRepository.getByName(orgSetting
				.get().getValue());

		if (orgByName == null)
			setErrorMessage(orgSetting.get(), "Organization '"
					+ orgSetting.get().getValue() + "' not found.");

		if (role != null && orgByName != null) {
			List<Role> rolesForOrganization = organizationRoleRepository
					.getRolesForOrganization(orgByName.getOrganizationId());

			boolean organizationDoesNotHaveRole = rolesForOrganization.stream()
					.noneMatch(
							r -> r.getRole().equalsIgnoreCase(role.getRole()));

			if (organizationDoesNotHaveRole) {
				setErrorMessage(roleSetting.get(), "Role '"
						+ roleSetting.get().getValue()
						+ "' does not belong to the selected organization.");
				setErrorMessage(orgSetting.get(), "Organization '"
						+ orgSetting.get().getValue()
						+ "' does not have selected role.");
			}
		}
	}

	/**
	 * Sets the error message or appends if the setting already has one.
	 * 
	 * @param setting
	 * @param message
	 */
	protected void setErrorMessage(AccountSetting setting, String message) {
		assert message != null;
		if (setting.getErrorMessage() != null)
			setting.setErrorMessage(setting.getErrorMessage() + ", " + message);
		else
			setting.setErrorMessage(message);
	}
}
