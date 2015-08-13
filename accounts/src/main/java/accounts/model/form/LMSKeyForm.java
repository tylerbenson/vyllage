package accounts.model.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class LMSKeyForm {

	private Long organizationId;
	private String consumerKey;
	private String secret;

	private String error;

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isInvalid() {
		return consumerKeyNotValid() || secretNotValid();
	}

	private boolean secretNotValid() {
		if (this.secret == null || this.secret.isEmpty()) {
			setError(getError() == null ? "Secret cannot be empty."
					: getError() + " Secret cannot be empty.");
			return true;
		}

		if (this.secret.length() < 10) {
			setError(getError() == null ? "Secret must be at least 10 characters long."
					: getError()
							+ " Secret must be at least 10 characters long.");
			return true;
		}

		return false;
	}

	private boolean consumerKeyNotValid() {
		if (this.consumerKey == null || this.consumerKey.isEmpty()) {
			setError(getError() == null ? "Consumer key cannot be empty."
					: getError() + " Consumer key cannot be empty.");
			return true;
		}

		return false;
	}

}
