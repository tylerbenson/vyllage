package oauth.lti;

import java.util.Optional;

import javax.inject.Inject;

import lombok.NonNull;
import oauth.repository.LTIKey;
import oauth.repository.LTIKeyRepository;
import oauth.utilities.LMSConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

import com.newrelic.api.agent.NewRelic;

@Component
public class LMSConsumerDetailsService implements ConsumerDetailsService {

	final static Logger log = LoggerFactory
			.getLogger(LMSConsumerDetailsService.class);

	private final LTIKeyRepository lMSKeyRepository;

	@Inject
	public LMSConsumerDetailsService(LTIKeyRepository lMSKeyRepository) {
		super();
		this.lMSKeyRepository = lMSKeyRepository;
	}

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(
			@NonNull final String consumerKey) throws OAuthException {

		final String trimConsumerKey = StringUtils.trimToNull(consumerKey);

		Assert.notNull(trimConsumerKey);

		final Optional<LTIKey> optional = lMSKeyRepository.get(trimConsumerKey);

		final LTIKey ltiKey;

		if (optional.isPresent())
			ltiKey = optional.get();
		else {

			OAuthException e = new OAuthException("Invalid key provided, key: "
					+ consumerKey);
			log.error(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);

			throw e;
		}

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
