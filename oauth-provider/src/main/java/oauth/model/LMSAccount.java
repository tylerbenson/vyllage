package oauth.model;

import lombok.ToString;

@ToString
public class LMSAccount {

	private String lmsId;
	private String lmsGuid;
	private String lmsName;
	private String lmsVersion;
	private LMSType type;
	private String ltiVersion;
	private String consumerKey;
	private Long lmsTypeId;
	private String oauthVersion;

	public LMSAccount() {

	}

	public LMSAccount(String lmsGuid, String lmsName, String lmsVersion,
			LMSType type, String ltiVersion, String oauthVersion) {
		super();
		this.setLmsGuid(lmsGuid);
		this.setLmsName(lmsName);
		this.setLmsVersion(lmsVersion);
		this.setType(type);
		this.setLtiVersion(ltiVersion);
		this.setOauthVersion(oauthVersion);
	}

	/**
	 * @return the lmsId
	 */
	public String getLmsId() {
		return lmsId;
	}

	/**
	 * @param lmsId
	 *            the lmsId to set
	 */
	public void setLmsId(String lmsId) {
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
	 * @return the type
	 */
	public LMSType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(LMSType type) {
		this.type = type;
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
	 * @return the oauthVersion
	 */
	public String getOauthVersion() {
		return oauthVersion;
	}

	/**
	 * @param oauthVersion
	 *            the oauthVersion to set
	 */
	public void setOauthVersion(String oauthVersion) {
		this.oauthVersion = oauthVersion;
	}

}
