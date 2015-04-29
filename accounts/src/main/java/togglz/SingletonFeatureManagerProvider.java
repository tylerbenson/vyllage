package togglz;

import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.spi.FeatureManagerProvider;

public class SingletonFeatureManagerProvider implements FeatureManagerProvider {

	private static FeatureManager featureManager;

	@Override
	public int priority() {
		return 30;
	}

	@Override
	public FeatureManager getFeatureManager() {
		if (featureManager == null) {
			featureManager = FeatureManagerBuilder.begin()
					.name("demo-feature-manager").featureEnum(Features.class)
					.togglzConfig(new TogglzConfiguration()).build();
		}
		return featureManager;
	}
}
