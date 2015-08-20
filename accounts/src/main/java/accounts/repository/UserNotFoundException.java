package accounts.repository;

public class UserNotFoundException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -6935651710822222357L;

	public UserNotFoundException(String msg) {
		super(msg);
	}
}
