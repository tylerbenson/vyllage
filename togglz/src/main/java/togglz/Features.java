package togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

	@EnabledByDefault
	@Label("Google Analytics")
	GOOGLE_ANALYTICS,

	@EnabledByDefault
	@Label("New Relic")
	NEW_RELIC,

	@EnabledByDefault
	@Label("Zopim Message Client")
	ZOPIM_MESSAGE_CLIENT,

	@Label("Share Resume")
	SHARE_RESUME;

	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
}
