package oauth.model.service;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import oauth.lti.LMSRequest;
import user.common.lms.LMSUserDetails;

public interface LMSUserDetailsService {

	/**
	 * @see UserDetailsService#loadUserByUsername(String)
	 */
	LMSUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException;

	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	/**
	 * Create a new user with the supplied details.
	 */
	void createUser(UserDetails user, LMSRequest lmsRequest);

	/**
	 * Update the specified user.
	 */
	void updateUser(UserDetails user);

	/**
	 * Remove the user with the given login name from the system.
	 */
	void deleteUser(String username);

	/**
	 * Modify the current user's password. This should change the user's
	 * password in the persistent user repository (datbase, LDAP etc).
	 *
	 * @param oldPassword
	 *            current password (for re-authentication if required)
	 * @param newPassword
	 *            the password to change to
	 */
	void changePassword(String oldPassword, String newPassword);

	/**
	 * Check if a user with the supplied login name exists in the system.
	 */
	boolean userExists(String username);
}
