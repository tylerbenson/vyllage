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

	/**
	 * 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	/**
	 * Create a new user with the supplied details.
	 */
	void createUser(UserDetails user, LMSRequest lmsRequest);

	/**
	 * Check if a user with the supplied login name exists in the system.
	 */
	boolean userExists(String username);
}
