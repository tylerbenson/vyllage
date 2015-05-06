package user.common;

import java.time.LocalDateTime;
import java.util.Collection;

import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

@ToString
public class User extends org.springframework.security.core.userdetails.User {

	private Long userId;

	private String firstName;

	private String middleName;

	private String lastName;

	private LocalDateTime dateCreated;

	private LocalDateTime lastModified;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7500528494734101041L;

	public User(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> userOrganizationRole) {
		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, userOrganizationRole);
		// this.setOrganizationMember(organizationMember);
	}

	public User(Long userId, String firstName, String middleName,
			String lastName, String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> userOrganizationRole,
			LocalDateTime dateCreated, LocalDateTime lastModified) {
		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, userOrganizationRole);
		this.userId = userId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.dateCreated = dateCreated;
		this.lastModified = lastModified;
		// this.setOrganizationMember(organizationMember);
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

}