package accounts.service;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import accounts.model.Authority;
import accounts.model.BatchAccount;
import accounts.model.GroupAuthority;
import accounts.model.User;
import accounts.model.UserFilterRequest;
import accounts.model.account.PersonalInformation;
import accounts.repository.AuthorityRepository;
import accounts.repository.GroupAuthorityRepository;
import accounts.repository.GroupRepository;
import accounts.repository.PersonalInformationRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;

@Service
public class UserService {
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(UserService.class.getName());

	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private GroupAuthorityRepository groupAuthorityRepository;

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private DocumentLinkService documentLinkService;

	@Autowired
	private PersonalInformationRepository pInformation;

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

		GroupAuthority groupAuthority = groupAuthorityRepository
				.getGroupAuthorityFromGroup(batchAccount.getGroup());

		List<User> users = Arrays
				.stream(emailSplit)
				.map(String::trim)
				.map(s -> new User(s, s, enabled, accountNonExpired,
						credentialsNonExpired, accountNonLocked, Arrays
								.asList(new Authority(groupAuthority
										.getAuthority(), s))))
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

		if (!userRepository.userExists(userName)) {
			User user = new User(userName, userName, true, true, true, true,
					authorityRepository
							.getDefaultAuthoritiesForNewUser(userName));
			userRepository.createUser(user);
		}
		User loadUserByUsername = userRepository.loadUserByUsername(userName);

		return loadUserByUsername;
	}

	public User getUser(Long userId) throws UserNotFoundException {
		return userRepository.get(userId);
	}

	public List<User> getAdvisors(UserFilterRequest filter, User loggedUser,
			int maxsize) {
		return userRepository.getAdvisors(filter, loggedUser, maxsize);
	}

	public List<User> getAdvisors(User loggedUser, int maxsize) {
		return userRepository.getAdvisors(loggedUser, maxsize);
	}

	public String getDefaultAuthority() {
		return "USER";
	}

	public String getDefaultGroup() {
		return "users";
	}

	public PersonalInformation getUserPersonalInformation(Long userId) {
		return pInformation.get(userId);
	}
}
