package oauth.repository;

import java.time.LocalDateTime;

import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

@ToString
public class LTIKey {
	private long keyId;

	private String keySha256;

	private String consumerKey;

	private String secret;

	private Long creatorUserId;

	private Long modifiedByUserId;

	private LocalDateTime lastModified;

	private LocalDateTime dateCreated;

	protected LTIKey() {
	}

	public LTIKey(String consumerKey, String secret) {
		this.consumerKey = consumerKey;
		this.keySha256 = makeSHA256(consumerKey);
		if (StringUtils.isNotBlank(secret)) {
			this.secret = secret;
		}
	}

	public long getKeyId() {
		return keyId;
	}

	public void setKeyId(long keyId) {
		this.keyId = keyId;
	}

	public String getKeySha256() {
		return keySha256;
	}

	public void setKeySha256(String keySha256) {
		this.keySha256 = keySha256;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String keyKey) {
		this.consumerKey = keyKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public int hashCode() {
		int result = (int) keyId;
		result = 31 * result + (keySha256 != null ? keySha256.hashCode() : 0);
		result = 31 * result + (consumerKey != null ? consumerKey.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;

		if (!(object instanceof LTIKey))
			return false;

		LTIKey other = (LTIKey) object;

		if (this.getCreatorUserId() == null ? other.getCreatorUserId() != null
				: !this.getCreatorUserId().equals(other.getCreatorUserId()))
			return false;

		if (this.getDateCreated() == null ? other.getDateCreated() != null
				: !this.getDateCreated().equals(other.getDateCreated()))
			return false;

		if (this.getKeyId() != other.getKeyId())
			return false;

		if (this.getConsumerKey() == null ? other.getConsumerKey() != null : !this
				.getConsumerKey().equals(other.getConsumerKey()))
			return false;

		if (this.getKeySha256() == null ? other.getKeySha256() != null : !this
				.getKeySha256().equals(other.getKeySha256()))
			return false;

		if (this.getModifiedByUserId() == null ? other.getLastModified() != null
				: !this.getLastModified().equals(other.getLastModified()))
			return false;

		if (this.getLastModified() == null ? other.getModifiedByUserId() != null
				: !this.getModifiedByUserId().equals(
						other.getModifiedByUserId()))
			return false;

		if (this.getSecret() == null ? other.getSecret() != null : !this
				.getSecret().equals(other.getSecret()))
			return false;

		return true;
	}

	public static String makeSHA256(String text) {
		String encode = null;
		if (StringUtils.isNotBlank(text)) {
			encode = org.apache.commons.codec.digest.DigestUtils
					.sha256Hex(text);
		}
		return encode;
	}

	public Long getCreatorUserId() {
		return creatorUserId;
	}

	public void setCreatorUserId(Long creatorUserId) {
		this.creatorUserId = creatorUserId;
	}

	public Long getModifiedByUserId() {
		return modifiedByUserId;
	}

	public void setModifiedByUserId(Long modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

}
