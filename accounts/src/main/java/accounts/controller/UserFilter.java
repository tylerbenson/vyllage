package accounts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import accounts.model.FilteredUser;
import accounts.model.User;
import accounts.model.UserFilterRequest;
import accounts.model.UserFilterResponse;
import accounts.repository.GroupRepository;
import accounts.service.UserService;

public class UserFilter {
	private static final int limitForEmptyFilter = 5;

	private static final int limitForFilter = 10;

	@Autowired
	private UserService service;

	@Autowired
	private GroupRepository groupRepository;

	public UserFilterResponse filter(UserFilterRequest filter, User loggedUser) {
		UserFilterResponse response = null;

		if (filter.isEmpty())
			response = getDefaultResponse(loggedUser);
		else
			response = getResponse(filter, loggedUser);

		return response;
	}

	protected UserFilterResponse getDefaultResponse(User loggedUser) {
		UserFilterResponse response = new UserFilterResponse();

		List<FilteredUser> collect = service
				.getAdvisors(loggedUser, limitForEmptyFilter).stream()
				.map(u -> new FilteredUser(u.getUsername()))
				.collect(Collectors.toList());

		response.setRecommended(collect);
		return response;
	}

	private UserFilterResponse getResponse(UserFilterRequest filter,
			User loggedUser) {
		UserFilterResponse response = new UserFilterResponse();

		List<FilteredUser> collect = service
				.getAdvisors(filter, loggedUser, limitForFilter).stream()
				.map(u -> new FilteredUser(u.getUsername()))
				.collect(Collectors.toList());

		response.setRecommended(collect);
		return response;
	}
}
