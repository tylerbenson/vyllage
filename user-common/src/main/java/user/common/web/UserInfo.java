package user.common.web;

import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import user.common.User;
import user.common.UserOrganizationRole;

/**
 * For user metadata in webpage.
 *
 * @author uh
 */
@ToString
public class UserInfo {

	private String email;
	private Long userId;

	// for intercom...
	private Long registeredOn;

	private Set<Long> organizationIds = new HashSet<>();

	private boolean emailConfirmed;

	public UserInfo(User user) {
		this.email = user.getUsername();
		this.userId = user.getUserId();

		if (user.getDateCreated() != null)
			this.registeredOn = user.getDateCreated().toInstant(ZoneOffset.UTC)
					.getEpochSecond();

		for (GrantedAuthority grantedAuthority : user.getAuthorities())
			getOrganizationIds().add(
					((UserOrganizationRole) grantedAuthority)
							.getOrganizationId());

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRegisteredOn() {
		return registeredOn;
	}

	public void setRegisteredOn(Long registeredOn) {
		this.registeredOn = registeredOn;
	}

	public Set<Long> getOrganizationIds() {
		return organizationIds;
	}

	public void setOrganizationIds(Set<Long> organizationIds) {
		this.organizationIds = organizationIds;
	}

	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

}
