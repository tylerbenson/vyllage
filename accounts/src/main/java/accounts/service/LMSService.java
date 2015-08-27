package accounts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;
import oauth.lti.LMSRequest;
import oauth.repository.LTIKeyRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import user.common.LMSUserCredentials;
import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.repository.LMSUserCredentialsRepository;
import accounts.repository.LMSUserRepository;
import accounts.repository.OrganizationRepository;
import accounts.repository.UserNotFoundException;

@Service
public class LMSService {

	private final Logger logger = Logger.getLogger(UserService.class.getName());
	private final LMSUserRepository lmsUserRepository;
	private final LMSUserCredentialsRepository lmsUserCredentialsRepository;
	private final LTIKeyRepository ltiKeyRepository;

	@Inject
	public LMSService(final OrganizationRepository organizationRepository,
			final LMSUserRepository lmsUserRepository,
			final LMSUserCredentialsRepository lmsUserCredentialsRepository,
			final LTIKeyRepository ltiKeyRepository) {
		super();
		this.lmsUserRepository = lmsUserRepository;
		this.lmsUserCredentialsRepository = lmsUserCredentialsRepository;
		this.ltiKeyRepository = ltiKeyRepository;
	}

	public User getUser(Long userId) throws UserNotFoundException {
		return lmsUserRepository.get(userId);
	}

	public boolean userExists(String userName) {
		return this.lmsUserRepository.userExists(userName);
	}

	public boolean lmsUserExists(String lmsUserId) {
		return this.lmsUserCredentialsRepository.userExists(lmsUserId);
	}

	public LMSUserCredentials getLmsUser(String lmsUserId)
			throws UserNotFoundException {
		return this.lmsUserCredentialsRepository.get(lmsUserId);
	}

	public Long getUserId(String lmsUserId) throws UserNotFoundException {
		return this.lmsUserCredentialsRepository.getUserId(lmsUserId);
	}

	public User createUser(@NonNull String email, @NonNull String password,
			String firstName, String middleName, String lastName,
			@NonNull LMSRequest lmsRequest) {

		final String consumerKey = lmsRequest.getLmsAccount().getConsumerKey();

		final Organization organization = ltiKeyRepository
				.getOrganizationByConsumerKey(consumerKey);

		final Long auditUserId = ltiKeyRepository.getAuditUser(consumerKey);

		if (auditUserId == null)
			throw new AccessDeniedException(
					"Vyllage account creation is not allowed without a referring user.");

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		List<UserOrganizationRole> defaultAuthoritiesForNewUser = new ArrayList<>();

		defaultAuthoritiesForNewUser.add(new UserOrganizationRole(null,
				organization.getOrganizationId(), RolesEnum.STUDENT.name(),
				auditUserId));

		/*
		 * UserOrganizationRole userOrganizationRole = new
		 * UserOrganizationRole(null, storedOrg.getOrganizationId(),
		 * lmsRequest.getLmsUser().getRole(), auditUser.getUserId());
		 * defaultAuthoritiesForNewUser.add(userOrganizationRole);
		 */
		User user = new User(null, firstName, middleName, lastName, email,
				password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, defaultAuthoritiesForNewUser, null, null);

		lmsUserRepository.createUser(user, lmsRequest);

		return lmsUserRepository.loadUserByUsername(user.getUsername());
	}
}
