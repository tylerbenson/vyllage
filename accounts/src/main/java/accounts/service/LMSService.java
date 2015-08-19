package accounts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.newrelic.api.agent.NewRelic;

import accounts.repository.LMSUserCredentialsRepository;
import accounts.repository.LMSUserRepository;
import accounts.repository.OrganizationRepository;
import accounts.repository.UserNotFoundException;
import lombok.NonNull;
import oauth.lti.LMSRequest;
import user.common.LMSUserCredentials;
import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;

@Service
public class LMSService {

	private final Logger logger = Logger.getLogger(UserService.class.getName());
	private final OrganizationRepository organizationRepository;
	private final LMSUserRepository lmsUserRepository;
	private final LMSUserCredentialsRepository lmsUserCredentialsRepository;

	@Inject
	public LMSService(final OrganizationRepository organizationRepository,
			final LMSUserRepository lmsUserRepository,
			final LMSUserCredentialsRepository lmsUserCredentialsRepository) {
		super();
		this.organizationRepository = organizationRepository;
		this.lmsUserRepository = lmsUserRepository;
		this.lmsUserCredentialsRepository = lmsUserCredentialsRepository;
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

		// TODO: get LMS Admin audit user id
		Long auditUserId = Long.valueOf(0);

		if (auditUserId == null)
			throw new AccessDeniedException(
					"Vyllage account creation is not allowed without a referring user.");

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User auditUser = null;
		try {
			auditUser = this.getUser(auditUserId);
		} catch (UserNotFoundException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		// add similar organization
		List<UserOrganizationRole> defaultAuthoritiesForNewUser = new ArrayList<>();

		for (GrantedAuthority userOrganizationRole : auditUser.getAuthorities()) {
			defaultAuthoritiesForNewUser.add(new UserOrganizationRole(null,
					((UserOrganizationRole) userOrganizationRole)
							.getOrganizationId(), RolesEnum.STUDENT.name(),
					auditUser.getUserId()));
			Organization organization = new Organization(
					((UserOrganizationRole) userOrganizationRole)
							.getOrganizationId(),
					null);
			lmsRequest.getLmsAccount().setOrganization(organization);
		}

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
