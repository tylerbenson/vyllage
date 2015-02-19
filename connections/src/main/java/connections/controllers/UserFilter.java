package connections.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import connections.model.Contact;
import connections.model.UserFilterRequest;
import connections.model.UserFilterResponse;

public class UserFilter {
	private static final long maxSize = 5;

	public UserFilterResponse filter(UserFilterRequest filter) {
		UserFilterResponse response = null;

		if (filter.isEmpty())
			response = getDefaultResponse();

		return response;
	}

	protected UserFilterResponse getDefaultResponse() {
		UserFilterResponse response = new UserFilterResponse();

		response.setRecent(getRecentlyUsed().stream().limit(maxSize)
				.collect(Collectors.toList()));
		response.setRecommended(getRecommended().stream().limit(maxSize)
				.collect(Collectors.toList()));

		return response;
	}

	protected List<Contact> getRecentlyUsed() {
		return new ArrayList<Contact>();
	}

	protected List<Contact> getRecommended() {
		return new ArrayList<Contact>();
	}
}
