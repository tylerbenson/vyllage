package login.model;

import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

@ToString
public class Authority implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6420872157496814372L;
	private String authority;

	@Override
	public String getAuthority() {
		return this.authority;
	}

	public void setSetAuthority(String authority) {
		this.authority = authority;
	}

}
