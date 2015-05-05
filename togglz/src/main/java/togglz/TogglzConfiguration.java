package togglz;

import javax.sql.DataSource;

import org.springframework.util.Assert;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.user.UserProvider;

public class TogglzConfiguration implements TogglzConfig {

	private DataSource dataSource;

	public TogglzConfiguration(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Class<? extends Feature> getFeatureClass() {
		return Features.class;
	}

	@Override
	public StateRepository getStateRepository() {

		Assert.notNull(dataSource);

		JDBCStateRepository jdbcStateRepository = new JDBCStateRepository(
				dataSource, "TOGGLZ.togglz_features");

		// final InMemoryStateRepository stateRepository = new
		// InMemoryStateRepository();
		// stateRepository.setFeatureState(new FeatureState(
		// Features.GOOGLE_ANALYTICS, true));

		return jdbcStateRepository;
	}

	@Override
	public UserProvider getUserProvider() {
		return new CustomSpringSecurityUserProvider("ADMIN", "STAFF");
	}

}
