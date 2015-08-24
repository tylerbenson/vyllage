package oauth.model.service;

import oauth.utilities.LMSConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;

@Component
public class LMSConsumerDetails implements ConsumerDetailsService {

	final static Logger log = LoggerFactory.getLogger(LMSConsumerDetails.class);

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String consumerKey)
			throws OAuthException {

		BaseConsumerDetails cd;
		if (LMSConstants.OAUTH_KEY.equals(consumerKey)) {
			cd = new BaseConsumerDetails();
			cd.setConsumerKey(consumerKey);
			cd.setSignatureSecret(new SharedConsumerSecretImpl(
					LMSConstants.OAUTH_SECRET));
			cd.setConsumerName(LMSConstants.SAMPLE_NAME);
			cd.setRequiredToObtainAuthenticatedToken(false);
			cd.getAuthorities().add(
					new SimpleGrantedAuthority(LMSConstants.ROLE_OAUTH));
		} else {
			throw new OAuthException("For this example, key must be 'key'");
		}
		return cd;
	}

}
