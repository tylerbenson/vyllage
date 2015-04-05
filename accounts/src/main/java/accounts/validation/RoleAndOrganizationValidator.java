package accounts.validation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import accounts.model.account.settings.AccountSetting;
import accounts.service.UserService;

public class RoleAndOrganizationValidator {

	@Autowired
	private UserService service;

	public void validate(List<AccountSetting> collect) {
		assert collect.size() == 2;

		Optional<AccountSetting> roleSetting = collect.stream()
				.filter(s -> s.getName().equalsIgnoreCase("role")).findFirst();
		Optional<AccountSetting> orgSetting = collect.stream()
				.filter(s -> s.getName().equalsIgnoreCase("organization"))
				.findFirst();

		assert roleSetting.isPresent() && orgSetting.isPresent();

	}

}
