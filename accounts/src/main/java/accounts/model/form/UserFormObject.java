package accounts.model.form;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import user.common.User;
import user.common.UserOrganizationRole;

/**
 * Represents a user for the forms
 *
 * @author uh
 *
 */
@ToString
public class UserFormObject {

	private Long userId;
	private String username;
	private List<Long> documents = new ArrayList<>();
	private String firstName;
	private String lastName;
	private List<UserOrganizationRole> authorities = new ArrayList<>();
	private boolean enabled;
	private boolean guest;

	public UserFormObject(User user) {
		super();
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.enabled = user.isEnabled();
		this.guest = user.isGuest();

		for (GrantedAuthority grantedAuthority : user.getAuthorities())
			this.authorities.add((UserOrganizationRole) grantedAuthority);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Long> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Long> documents) {
		this.documents = documents;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<UserOrganizationRole> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<UserOrganizationRole> authorities) {
		this.authorities = authorities;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isGuest() {
		return guest;
	}

	public void setGuest(boolean guest) {
		this.guest = guest;
	}
}
