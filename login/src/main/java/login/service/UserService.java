package login.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import login.model.Authority;
import login.model.BatchAccount;
import login.model.GroupAuthority;
import login.model.UserFilterRequest;
import login.repository.AuthorityRepository;
import login.repository.GroupAuthorityRepository;
import login.repository.GroupRepository;
import login.repository.UserDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private GroupAuthorityRepository groupAuthorityRepository;

	@Autowired
	private UserDetailRepository userRepository;

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
}
