package accounts.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class User extends org.springframework.security.core.userdetails.User {

	private Long userId;

	private String firstName;

	private String middleName;

	private String lastName;

	private LocalDateTime dateCreated;

	private LocalDateTime lastModified;

	private List<OrganizationMember> organizationMember = new ArrayList<>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 7500528494734101041L;

	public User(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> roles,
			List<OrganizationMember> organizationMember) {
		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, roles);
		this.setOrganizationMember(organizationMember);
	}

	public User(String userName, String randomPassword, List<UserRole> roles,
			List<OrganizationMember> organizationMember) {
		super(userName, randomPassword, roles);
		this.setOrganizationMember(organizationMember);
	}

	public User(Long userId, String firstName, String middleName,
			String lastName, String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,
			List<OrganizationMember> organizationMember,
			LocalDateTime dateCreated, LocalDateTime lastModified) {
		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, authorities);
		this.userId = userId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.dateCreated = dateCreated;
		this.lastModified = lastModified;
		this.setOrganizationMember(organizationMember);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public List<OrganizationMember> getOrganizationMember() {
		return organizationMember;
	}

	public void setOrganizationMember(List<OrganizationMember> organizationMember) {
		this.organizationMember = organizationMember;
	}

}
