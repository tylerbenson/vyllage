package togglz.controller;

import java.util.HashMap;
import java.util.Map;

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

		Map<String, Boolean> map = new HashMap<>();
		map.put("value", value.isActive());

		if (value.isActive())
			return new ResponseEntity<Map<String, Boolean>>(map, HttpStatus.OK);
		return new ResponseEntity<Map<String, Boolean>>(map,
				HttpStatus.ACCEPTED);
	}
}
