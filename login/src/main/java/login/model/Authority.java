package login.model;

import login.domain.tables.records.AuthoritiesRecord;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

@ToString
@EqualsAndHashCode
public class Authority implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6420872157496814372L;
	private String authority;
	private String userName;

	public Authority() {
	}

	public Authority(String authority, String userName) {
		this.authority = authority;
		this.userName = userName;

	}

	public Authority(AuthoritiesRecord record) {
		this.authority = record.getAuthority();
		this.userName = record.getUsername();
	}

	@Override
	public String getAuthority() {
		return this.authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
