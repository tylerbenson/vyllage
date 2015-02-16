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

@Service
public class AccountService {
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private UserDetailRepository userRepository;

	public void batchCreateUsers(BatchAccount batchAccount) {

		final boolean enabled = true;
		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		final List<Authority> authority = authorityRepository
				.getAuthorityFromGroup(batchAccount.getGroup());

		String accounts = batchAccount.getEmails();

		String[] emailSplit = accounts.split(",");

		List<User> users = Arrays
				.stream(emailSplit)
				.map(s -> s.trim())
				.map(s -> new User(s, s, enabled, accountNonExpired,
						credentialsNonExpired, accountNonLocked, authority))
				.collect(Collectors.toList());

		userRepository.saveUsers(users);
	}
}
