package user.common.web;

import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import user.common.User;
import user.common.UserOrganizationRole;

/**
 * For user metadata in webpage.
 *
 * @author uh
 *
 */
@ToString
public class UserInfo {

	private String email;
	private Long userId;

	// for intercom...
	private Long registeredOn;

	private List<Long> organizationIds = new LinkedList<>();

	public UserInfo(User user) {
		this.email = user.getUsername();
		this.userId = user.getUserId();
		this.registeredOn = user.getDateCreated().toInstant(ZoneOffset.UTC)
				.getEpochSecond();
		for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
			organizationIds.add(((UserOrganizationRole) grantedAuthority)
					.getOrganizationId());
		}
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

	public List<Long> getOrganizationIds() {
		return organizationIds;
	}

	public void setOrganizationIds(List<Long> organizationIds) {
		this.organizationIds = organizationIds;
	}

}
