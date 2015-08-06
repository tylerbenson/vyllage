/**
 * 
 */
package user.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kunal.shankar
 *
 */
@ToString
@EqualsAndHashCode
public class LMSUserCredentials {

	private String lmsUserId;
	private String password;
	private boolean enabled;
	private Timestamp expires;
	private Long lmsId;
	private Long userId;

	/**
	 * @return the lmsUserId
	 */
	public String getLmsUserId() {
		return lmsUserId;
	}

	/**
	 * @param lmsUserId
	 *            the lmsUserId to set
	 */
	public void setLmsUserId(String lmsUserId) {
		this.lmsUserId = lmsUserId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the expires
	 */
	public Timestamp getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(Timestamp expires) {
		this.expires = expires;
	}

	/**
	 * @return the lmsId
	 */
	public Long getLmsId() {
		return lmsId;
	}

	/**
	 * @param lmsId the lmsId to set
	 */
	public void setLmsId(Long lmsId) {
		this.lmsId = lmsId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
