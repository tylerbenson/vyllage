package togglz;

import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.user.UserProvider;
import org.togglz.spring.security.SpringSecurityUserProvider;

public class TogglzConfiguration implements TogglzConfig {

	@Override
	public Class<? extends Feature> getFeatureClass() {
		return Features.class;
	}

	@Override
	public StateRepository getStateRepository() {
		// return new FileBasedStateRepository(
		// new File("/tmp/features.properties"));
		// return stateRepository;

		final InMemoryStateRepository stateRepository = new InMemoryStateRepository();
		stateRepository.setFeatureState(new FeatureState(
				Features.GOOGLE_ANALYTICS, true));
		return stateRepository;
	}

	@Override
	public UserProvider getUserProvider() {
		return new SpringSecurityUserProvider("ADMIN");
		// return new UserProvider() {
		// @Override
		// public FeatureUser getCurrentUser() {
		// return new SimpleFeatureUser("ADMIN", true);
		// }
		// };
	}

}
