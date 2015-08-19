package user.common.lms;

import org.springframework.security.core.userdetails.UserDetails;

public interface LMSUserDetails extends UserDetails {

	/**
	 * The user's identity at the provider. Might be same as
	 * {@link #getUsername()} if users are identified by username
	 * 
	 * @return user's id used to assign connections
	 */
	String getUserId();

}
