package login.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import login.model.Authority;
import login.model.BatchAccount;
import login.repository.AuthorityRepository;
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

	public void createUser(String username) {
		boolean invalid = false;

		if (EmailValidator.validate(username) == invalid)
			throw new IllegalArgumentException(
					"Contains invalid email addresses.");

		User user = new User(username, getRandomPassword(),
				authorityRepository.getDefaultAuthoritiesForNewUser(username));
	}

	private String getRandomPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}
