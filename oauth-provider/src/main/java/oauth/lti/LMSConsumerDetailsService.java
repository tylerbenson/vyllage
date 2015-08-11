package oauth.lti;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;

import oauth.utilities.LMSConstants;

@Component
public class LMSConsumerDetailsService implements ConsumerDetailsService {

	final static Logger log = LoggerFactory.getLogger(LMSConsumerDetailsService.class);
	private final Environment environment;

	@Inject
	public LMSConsumerDetailsService(final Environment environment) {
		super();
		this.environment = environment;
	}

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {

		consumerKey = StringUtils.trimToNull(consumerKey);
		BaseConsumerDetails cd;
		LMSKey ltiKey = new LMSKey(environment.getProperty(LMSConstants.OAUTH_KEY),
				environment.getProperty(LMSConstants.OAUTH_SECRET));

		cd = new BaseConsumerDetails();
		cd.setConsumerKey(consumerKey);
		cd.setSignatureSecret(new SharedConsumerSecretImpl(ltiKey.getSecret()));
		cd.setConsumerName(String.valueOf(ltiKey.getKeyId()));
		cd.setRequiredToObtainAuthenticatedToken(false);
		cd.getAuthorities().add(new SimpleGrantedAuthority(LMSConstants.ROLE_OAUTH));
		cd.getAuthorities().add(new SimpleGrantedAuthority(LMSConstants.ROLE_LTI));
		return cd;
	}
}

class LMSKey {

	private long keyId;

	private String keySha256;

	private String keyKey;

	private String secret;

	protected LMSKey() {
	}

	public LMSKey(String key, String secret) {
		this.keyKey = key;
		this.keySha256 = makeSHA256(key);
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

	public String getKeyKey() {
		return keyKey;
	}

	public void setKeyKey(String keyKey) {
		this.keyKey = keyKey;
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
		result = 31 * result + (keyKey != null ? keyKey.hashCode() : 0);
		return result;
	}

	public static String makeSHA256(String text) {
		String encode = null;
		if (StringUtils.isNotBlank(text)) {
			encode = org.apache.commons.codec.digest.DigestUtils.sha256Hex(text);
		}
		return encode;
	}
}