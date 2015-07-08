package oauth.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Service;

@Service
public class OAuthConsumerDetailsService implements ConsumerDetailsService
{
	//TODO: Static consumerName & pick these values from properties files.
	String consumerName = "blackboard" ;	
    String consumerKey = "3a4393c3da1a4e316ee66c0cc61c71";
    String consumerSecret = "fe1372c074185b19c309964812bb8f3f2256ba514aea8a318";
    
    //@Autowired
	//private Environment environment;
    // environment.getProperty("spring.oauth.consumerKey");
    //environment.getProperty("spring.oauth.consumerSecret");
    
    public OAuthConsumerDetailsService() {}
 
 
    @Override
    public ConsumerDetails loadConsumerByConsumerKey( String consumerKey ) throws OAuthException
    {
        if( consumerKey == null )
            throw new OAuthException("No credentials found for the consumer key [" + consumerKey + "]");
 
        if( !consumerKey.equals( this.consumerKey ) )
            throw new OAuthException("No credentials found for the consumer key [" + consumerKey + "]");
 
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add( new SimpleGrantedAuthority("ROLE_OAUTH") );
 
        return new OAuthConsumerDetails(
                consumerName,
                consumerKey,
                consumerSecret,
                authorities );
    }
 
}