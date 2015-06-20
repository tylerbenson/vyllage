package accounts.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.ToString;
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

	@SuppressWarnings("unchecked")
	public UserFormObject(User user) {
		super();
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.authorities = (List<UserOrganizationRole>) user.getAuthorities()
				.stream().collect(Collectors.toList());

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
}
