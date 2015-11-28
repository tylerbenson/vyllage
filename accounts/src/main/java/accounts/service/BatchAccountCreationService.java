package accounts.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.User;
import user.common.UserOrganizationRole;
import accounts.model.BatchAccount;
import accounts.model.BatchResult;
import accounts.repository.BatchAccountCreationRepository;
import accounts.repository.UserDetailRepository;
import accounts.service.utilities.BatchParser;
import accounts.service.utilities.BatchParser.ParsedAccount;
import accounts.service.utilities.RandomPasswordGenerator;

@Service
public class BatchAccountCreationService {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(BatchAccountCreationService.class.getName());

	private final BatchAccountCreationRepository batchAccountCreationRepository;

	private final UserDetailRepository userDetailRepository;

	private final RegistrationEmailService registrationEmailService;

	private final RandomPasswordGenerator randomPasswordGenerator;

	private final BatchParser batchParser;

	@Inject
	public BatchAccountCreationService(
			RegistrationEmailService registrationEmailService,
			BatchAccountCreationRepository batchAccountCreationRepository,
			UserDetailRepository userDetailRepository,
			RandomPasswordGenerator randomPasswordGenerator,
			BatchParser batchParser) {
		this.registrationEmailService = registrationEmailService;
		this.batchAccountCreationRepository = batchAccountCreationRepository;
		this.userDetailRepository = userDetailRepository;
		this.randomPasswordGenerator = randomPasswordGenerator;
		this.batchParser = batchParser;
	}

	public BatchResult batchCreateUsers(@NonNull BatchAccount batchAccount,
			@NonNull User loggedInUser, boolean forcePasswordChange)
			throws IOException {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Assert.notNull(batchAccount.getOrganization());
		Assert.notNull(batchAccount.getRole());
		Assert.notNull(batchAccount.getTxt());

		List<ParsedAccount> parsedAccounts = this.batchParser
				.parse(batchAccount.getTxt());

		// note, for UserOrganizationRole there's no userId until it's
		// saved.
		String randomPassword = this.randomPasswordGenerator
				.getRandomPassword();

		List<User> users = parsedAccounts
				.stream()
				.map(pa -> new User(null, pa.getFirstName(),
						pa.getMiddleName(), pa.getLastName(), pa.getEmail(),
						randomPassword, enabled, accountNonExpired,
						credentialsNonExpired, accountNonLocked, Arrays
								.asList(new UserOrganizationRole(null,
										batchAccount.getOrganization(),
										batchAccount.getRole(), loggedInUser
												.getUserId())), null, null))
				.collect(Collectors.toList());

		final BatchResult result = new BatchResult();

		// check if the user already exists and remove.
		final List<User> filteredUsers = users.stream().filter(u -> {
			if (this.userDetailRepository.userExists(u.getUsername())) {
				result.addUserNameExists(u.getUsername());
				return false; // remove them
			}
			return true;
		}).collect(Collectors.toList());

		this.batchAccountCreationRepository.createUsers(filteredUsers,
				loggedInUser, forcePasswordChange);

		// send mails
		for (User user : filteredUsers) {
			this.registrationEmailService.sendAutomatedAccountCreationEmail(
					user.getUsername(), randomPassword, user.getFirstName());
		}

		return result;
	}

}
