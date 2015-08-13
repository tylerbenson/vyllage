package oauth.lti;

import javax.inject.Inject;

import oauth.repository.LMSKey;
import oauth.repository.LMSKeyRepository;
import oauth.utilities.LMSConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LMSConsumerDetailsService implements ConsumerDetailsService {

	final static Logger log = LoggerFactory
			.getLogger(LMSConsumerDetailsService.class);

	private final LMSKeyRepository lMSKeyRepository;

	@Inject
	public LMSConsumerDetailsService(LMSKeyRepository lMSKeyRepository) {
		super();
		this.lMSKeyRepository = lMSKeyRepository;
	}

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String consumerKey)
			throws OAuthException {

		consumerKey = StringUtils.trimToNull(consumerKey);

		Assert.notNull(consumerKey);

		LMSKey ltiKey = lMSKeyRepository.get(consumerKey);

		BaseConsumerDetails cd = new BaseConsumerDetails();
		cd.setConsumerKey(consumerKey);
		cd.setSignatureSecret(new SharedConsumerSecretImpl(ltiKey.getSecret()));
		cd.setConsumerName(String.valueOf(ltiKey.getKeyId()));
		cd.setRequiredToObtainAuthenticatedToken(false);
		cd.getAuthorities().add(
				new SimpleGrantedAuthority(LMSConstants.ROLE_OAUTH));
		cd.getAuthorities().add(
				new SimpleGrantedAuthority(LMSConstants.ROLE_LTI));
		return cd;
	}
}