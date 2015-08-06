/**
 * 
 */
package user.common;

import java.sql.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author kunal.shankar
 *
 */
@ToString
@EqualsAndHashCode
public class LMS {

	private Long lmsId;
	private String lmsGuid;
	private String lmsName;
	private String lmsVersion;
	private Long lmsTypeId;
	private String ltiVersion;
	private String oAuthVersion;
	private String consumerKey;
	private Long organizationId;
	private Timestamp dateCreated;
	private Timestamp lastModified;

	/**
	 * @return the lmsId
	 */
	public Long getLmsId() {
		return lmsId;
	}

	/**
	 * @param lmsId
	 *            the lmsId to set
	 */
	public void setLmsId(Long lmsId) {
		this.lmsId = lmsId;
	}

	/**
	 * @return the lmsGuid
	 */
	public String getLmsGuid() {
		return lmsGuid;
	}

	/**
	 * @param lmsGuid
	 *            the lmsGuid to set
	 */
	public void setLmsGuid(String lmsGuid) {
		this.lmsGuid = lmsGuid;
	}

	/**
	 * @return the lmsName
	 */
	public String getLmsName() {
		return lmsName;
	}

	/**
	 * @param lmsName
	 *            the lmsName to set
	 */
	public void setLmsName(String lmsName) {
		this.lmsName = lmsName;
	}

	/**
	 * @return the lmsVersion
	 */
	public String getLmsVersion() {
		return lmsVersion;
	}

	/**
	 * @param lmsVersion
	 *            the lmsVersion to set
	 */
	public void setLmsVersion(String lmsVersion) {
		this.lmsVersion = lmsVersion;
	}

	/**
	 * @return the lmsTypeId
	 */
	public Long getLmsTypeId() {
		return lmsTypeId;
	}

	/**
	 * @param lmsTypeId
	 *            the lmsTypeId to set
	 */
	public void setLmsTypeId(Long lmsTypeId) {
		this.lmsTypeId = lmsTypeId;
	}

	/**
	 * @return the ltiVersion
	 */
	public String getLtiVersion() {
		return ltiVersion;
	}

	/**
	 * @param ltiVersion
	 *            the ltiVersion to set
	 */
	public void setLtiVersion(String ltiVersion) {
		this.ltiVersion = ltiVersion;
	}

	/**
	 * @return the oAuthVersion
	 */
	public String getoAuthVersion() {
		return oAuthVersion;
	}

	/**
	 * @param oAuthVersion
	 *            the oAuthVersion to set
	 */
	public void setoAuthVersion(String oAuthVersion) {
		this.oAuthVersion = oAuthVersion;
	}

	/**
	 * @return the consumerKey
	 */
	public String getConsumerKey() {
		return consumerKey;
	}

	/**
	 * @param consumerKey
	 *            the consumerKey to set
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	/**
	 * @return the organizationId
	 */
	public Long getOrganizationId() {
		return organizationId;
	}

	/**
	 * @param organizationId
	 *            the organizationId to set
	 */
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * @return the dateCreated
	 */
	public Timestamp getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the lastModified
	 */
	public Timestamp getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

}
