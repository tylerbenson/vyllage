package accounts.model;

/**
 * Hmm..
 * 
 * @author uh
 *
 */
public class UserNameAndId {

	private Long userId;
	private String username;

	public UserNameAndId(Long userId, String username) {
		super();
		this.userId = userId;
		this.username = username;
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
}
