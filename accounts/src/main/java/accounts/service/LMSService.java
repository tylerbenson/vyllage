package accounts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
import oauth.lti.LMSRequest;
import user.common.LMSUserCredentials;
import user.common.Organization;
import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;

@Service
public class LMSService {

	private final Logger logger = Logger.getLogger(UserService.class.getName());

	@Autowired
	OrganizationRepository organizationRepository;

	@Autowired
	private LMSUserRepository lmsUserRepository;

	@Autowired
	private LMSUserCredentialsRepository lmsUserCredentialsRepository;

	public User getUser(Long userId) throws UserNotFoundException {
		return lmsUserRepository.get(userId);
	}

	public boolean userExists(String userName) {
		return this.lmsUserRepository.userExists(userName);
	}

	public boolean lmsUserExists(String lmsUserId) {
		return this.lmsUserCredentialsRepository.userExists(lmsUserId);
	}

	public LMSUserCredentials getLmsUser(String lmsUserId) {
		try {
			return this.lmsUserCredentialsRepository.get(lmsUserId);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Long getUserId(String lmsUserId) {
		try {
			return this.lmsUserCredentialsRepository.getUserId(lmsUserId);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User createUser(String email, String password, String firstName, String middleName, String lastName,
			LMSRequest lmsRequest) {

		Organization organization = lmsRequest.getLmsAccount().getOrganization();
		Organization storedOrg = organizationRepository.getByName(organization.getOrganizationName());
		if (storedOrg == null) {
			storedOrg = organizationRepository.addOrganization(lmsRequest.getLmsAccount().getOrganization());
		}
		lmsRequest.getLmsAccount().setOrganization(storedOrg);

		// TODO to get LMS audit user id
		Long auditUserId = Long.valueOf(0);

		if (auditUserId == null)
			throw new AccessDeniedException("Account creation is not allowed without a referring user.");

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
			defaultAuthoritiesForNewUser.add(
					new UserOrganizationRole(null, ((UserOrganizationRole) userOrganizationRole).getOrganizationId(),
							RolesEnum.GUEST.name(), auditUser.getUserId()));
		}

		/*
		 * UserOrganizationRole userOrganizationRole = new
		 * UserOrganizationRole(null, storedOrg.getOrganizationId(),
		 * lmsRequest.getLmsUser().getRole(), auditUser.getUserId());
		 * defaultAuthoritiesForNewUser.add(userOrganizationRole);
		 */
		User user = new User(null, firstName, middleName, lastName, email, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, defaultAuthoritiesForNewUser, null, null);

		lmsUserRepository.createUser(user, lmsRequest);

		return lmsUserRepository.loadUserByUsername(user.getUsername());
	}
}
