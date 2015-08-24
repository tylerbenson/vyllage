package accounts.config;

import javax.sql.DataSource;

import org.springframework.util.Assert;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.spi.FeatureManagerProvider;

import togglz.Features;
import togglz.TogglzConfiguration;

/**
 * Not really a singleton, but this one works.
 *
 * @author uh
 *
 */
public class SingletonFeatureManagerProvider implements FeatureManagerProvider {

	private FeatureManager featureManager;

	public SingletonFeatureManagerProvider() {

	}

	@Override
	public int priority() {
		return 30;
	}

	@Override
	public FeatureManager getFeatureManager() {

		if (featureManager == null) {
			DataSource dataSource = (DataSource) ApplicationContextProvider
					.getApplicationContext().getBean("dataSource");

			Assert.notNull(dataSource);

			featureManager = FeatureManagerBuilder.begin().name("features")
					.featureEnum(Features.class)
					.togglzConfig(new TogglzConfiguration(dataSource)).build();
		}

		Assert.notNull(featureManager);

		return featureManager;
	}
}
