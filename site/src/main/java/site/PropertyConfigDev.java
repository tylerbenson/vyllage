package site;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import util.profiles.Profiles;

@PropertySource("classpath:/connections/application-dev.properties")
@PropertySource("classpath:/documents/application-dev.properties")
@PropertySource("classpath:/accounts/application-dev.properties")
@Configuration(value = "site.PropertyConfigDev")
@Profile(Profiles.DEV)
// @ConditionalOnMissingBean(name = "site.PropertyConfigProd")
public class PropertyConfigDev {

}
