package login.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import login.model.Authority;
import login.model.BatchAccount;
import login.model.User;
import login.repository.AuthorityRepository;
import login.repository.GroupRepository;
import login.repository.UserDetailRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private DocumentLinkService documentLinkService;

	public User getUser(String username) {
		return this.userRepository.loadUserByUsername(username);
	}

	public List<User> getAllUsers() {
		return this.userRepository.getAll();
	}

	public boolean userExists(String userName) {
		return this.userRepository.userExists(userName);
	}

	public void batchCreateUsers(BatchAccount batchAccount)
			throws IllegalArgumentException {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		Assert.notNull(batchAccount.getGroup());
		Assert.notNull(batchAccount.getEmails());

		final List<Authority> authority = authorityRepository
				.getAuthorityFromGroup(batchAccount.getGroup());

		String[] emailSplit = batchAccount.getEmails()
				.replace(";", System.lineSeparator())
				.replace(",", System.lineSeparator()).trim()
				.split(System.lineSeparator());

		boolean invalid = Arrays.stream(emailSplit).map(String::trim)
				.map(EmailValidator::validate).collect(Collectors.toList())
				.stream().anyMatch(p -> p.booleanValue() != true);

		if (invalid)
			throw new IllegalArgumentException(
					"Contains invalid email addresses.");

		List<User> users = Arrays
				.stream(emailSplit)
				.map(String::trim)
				.map(s -> new User(s, s, enabled, accountNonExpired,
						credentialsNonExpired, accountNonLocked, authority))
				.collect(Collectors.toList());

		userRepository.saveUsers(users);
	}

	/**
	 * Process a link request, if the user doesn't exist, creates one with a
	 * random password.
	 * 
	 * @param linkRequest
	 * @return link response
	 */
	public User createUser(String userName) {
		boolean invalid = false;

		if (EmailValidator.validate(userName) == invalid)
			throw new IllegalArgumentException(
					"Contains invalid email addresses.");

		String randomPassword = getRandomPassword();

		// TODO: call email service to email the user his account
		// password if the user didn't exist.

		if (!userRepository.userExists(userName)) {

			User user = new User(userName, randomPassword,
					authorityRepository
							.getDefaultAuthoritiesForNewUser(userName));
			userRepository.createUser(user);
		}

		return this.getUser(userName);
	}

	private String getRandomPassword() {
		return RandomStringUtils.random(60);
	}
}
