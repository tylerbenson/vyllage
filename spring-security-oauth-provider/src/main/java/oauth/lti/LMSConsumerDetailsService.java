package oauth.lti;


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

import oauth.utilities.Contant;
import oauth.vo.LMSKey;

@Component
public class LMSConsumerDetailsService implements ConsumerDetailsService {

    final static Logger log = LoggerFactory.getLogger(LMSConsumerDetailsService.class);


    @Override
    public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
    	
        consumerKey = StringUtils.trimToNull(consumerKey);
        BaseConsumerDetails cd;
        
        //TODO: Read the key and secret value from properties file 
        //LMSKey ltiKey = new LMSKey(environment.getProperty(Contant.OAUTH_KEY), environment.getProperty(Contant.OAUTH_SECRET)) ;
        
        LMSKey ltiKey = new LMSKey(Contant.OAUTH_KEY, Contant.OAUTH_SECRET) ;
        
        cd = new BaseConsumerDetails();
        cd.setConsumerKey(consumerKey);
        cd.setSignatureSecret(new SharedConsumerSecretImpl(ltiKey.getSecret()));
        cd.setConsumerName(String.valueOf(ltiKey.getKeyId()));
        cd.setRequiredToObtainAuthenticatedToken(false); 
        cd.getAuthorities().add(new SimpleGrantedAuthority(Contant.ROLE_OAUTH)); 
        cd.getAuthorities().add(new SimpleGrantedAuthority(Contant.ROLE_LTI));
        return cd;
    }

}