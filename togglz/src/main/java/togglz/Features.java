package togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

	@Label("Dummy Suggestions")
	DUMMY_SUGGESTIONS,

	@EnabledByDefault
	@Label("Facebook SDK")
	FACEBOOK_SDK,

	@EnabledByDefault
	@Label("Google Analytics")
	GOOGLE_ANALYTICS,

	@EnabledByDefault
	@Label("Intercom")
	INTERCOM,

	@EnabledByDefault
	@Label("New Relic")
	NEW_RELIC,

	@Label("Printing")
	PRINTING,

	@EnabledByDefault
	@Label("Share Resume")
	SHARE_RESUME,

	@Label("Suggestions")
	SUGGESTIONS,

	@EnabledByDefault
	@Label("Zopim Message Client")
	ZOPIM_MESSAGE_CLIENT;

	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
}
