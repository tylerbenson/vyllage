package togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

	// @EnabledByDefault
	@Label("Google Analytics")
	GOOGLE_ANALYTICS;

	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
}
