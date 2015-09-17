package accounts.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import user.common.User;
import user.common.UserOrganizationRole;
import accounts.model.BatchAccount;
import accounts.model.BatchResult;
import accounts.repository.UserDetailRepository;
import accounts.service.utilities.BatchParser;
import accounts.service.utilities.BatchParser.ParsedAccount;
import accounts.service.utilities.RandomPasswordGenerator;

import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

@Service
public class BatchAccountCreationService {
	private final Logger logger = Logger
			.getLogger(BatchAccountCreationService.class.getName());

	private final UserDetailRepository userRepository;

	private final ExecutorService executorService;

	private final EmailBuilder emailBuilder;

	private final RandomPasswordGenerator randomPasswordGenerator;

	private final BatchParser batchParser;

	private final Environment environment;

	@Inject
	public BatchAccountCreationService(
			UserDetailRepository userRepository,
			@Qualifier(value = "accounts.ExecutorService") ExecutorService executorService,
			@Qualifier(value = "accounts.emailBuilder") EmailBuilder emailBuilder,
			RandomPasswordGenerator randomPasswordGenerator,
			BatchParser batchParser, Environment environment) {
		this.userRepository = userRepository;
		this.executorService = executorService;
		this.emailBuilder = emailBuilder;
		this.randomPasswordGenerator = randomPasswordGenerator;
		this.batchParser = batchParser;
		this.environment = environment;
	}

	public BatchResult batchCreateUsers(BatchAccount batchAccount,
			User loggedInUser, boolean forcePasswordChange) throws IOException {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Assert.notNull(batchAccount.getOrganization());
		Assert.notNull(batchAccount.getRole());
		Assert.notNull(batchAccount.getTxt());

		List<ParsedAccount> parsedAccounts = batchParser.parse(batchAccount
				.getTxt());

		// note, for UserOrganizationRole there's no userId until it's
		// saved.
		String randomPassword = randomPasswordGenerator.getRandomPassword();

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
			if (userRepository.userExists(u.getUsername())) {
				result.addUserNameExists(u.getUsername());
				return false; // remove them
			}
			return true;
		}).collect(Collectors.toList());

		userRepository.addUsers(filteredUsers, loggedInUser,
				forcePasswordChange);

		// send mails
		for (User user : filteredUsers) {
			sendAutomatedAccountCreationEmail(user.getUsername(),
					randomPassword, user.getFirstName());
		}

		return result;
	}

	protected void sendAutomatedAccountCreationEmail(String email,
			String password, String firstName) {
		Runnable run = () -> {

			try {
				emailBuilder
						.to(email)
						.from(environment.getProperty("email.from",
								"no-reply@vyllage.com"))
						.fromUserName(
								environment.getProperty("email.from.userName",
										"Chief of Vyllage"))
						.subject("Account Creation - Vyllage.com")
						.setNoHtmlMessage(
								"Your account has been created successfuly. \\n Your password is: "
										+ password)
						.templateName("email-account-created")
						.addTemplateVariable("password", password)
						.addTemplateVariable("firstName", firstName).send();
			} catch (EmailException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		executorService.execute(run);

	}

}
