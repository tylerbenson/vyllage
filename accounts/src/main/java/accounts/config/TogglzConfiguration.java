package accounts.config;

import java.io.File;

import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.user.UserProvider;
import org.togglz.spring.security.SpringSecurityUserProvider;

import togglz.Features;

@Component
public class TogglzConfiguration implements TogglzConfig {

	@Override
	public Class<? extends Feature> getFeatureClass() {
		return Features.class;
	}

	@Override
	public StateRepository getStateRepository() {
		return new FileBasedStateRepository(
				new File("/tmp/features.properties"));
	}

	@Override
	public UserProvider getUserProvider() {
		return new SpringSecurityUserProvider("ADMIN");
	}

}
