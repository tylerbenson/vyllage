package togglz.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import togglz.Features;

@RestController
@RequestMapping("togglz-feature")
public class TogglzFeatureController {

	/**
	 * Returns if a certain feature is enabled or not.
	 * 
	 * HtttpStatus 200 for True. <br>
	 * HtttpStatus 202 for False. <br>
	 * HtttpStatus 404 if the feature doesn't exist.
	 * 
	 * @param feature
	 * @return true: enabled, false: disabled.
	 */
	@RequestMapping(value = "{feature}/is-active", method = RequestMethod.GET)
	public ResponseEntity<?> isEnabled(@PathVariable String feature) {

		if (feature == null || feature.isEmpty())
			return new ResponseEntity<String>("Feature name cannot be null.",
					HttpStatus.BAD_REQUEST);

		Features value;

		try {
			value = Features.valueOf(feature.toUpperCase());
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<String>("No feature named '" + feature
					+ "' found.", HttpStatus.BAD_REQUEST);
		}

		if (value.isActive())
			return new ResponseEntity<Boolean>(value.isActive(), HttpStatus.OK);
		return new ResponseEntity<Boolean>(value.isActive(),
				HttpStatus.ACCEPTED);
	}
}
