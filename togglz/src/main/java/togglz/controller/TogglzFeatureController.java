package togglz.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import togglz.Features;

@RestController
@RequestMapping("togglz-feature")
public class TogglzFeatureController {

	@RequestMapping(value = "{feature}/is-active", method = RequestMethod.GET)
	public boolean isEnabled(@PathVariable String feature) {

		if (feature == null || feature.isEmpty())
			throw new IllegalArgumentException("Feature name cannot be null.");

		Features value;

		try {
			value = Features.valueOf(feature.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("No feature named '" + feature
					+ "' found.");
		}

		return value.isActive();
	}

}
