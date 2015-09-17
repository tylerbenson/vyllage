package accounts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.ToString;

@ToString
public class BatchResult {

	private final List<String> existingUsers = new ArrayList<>();

	public void addUserNameExists(String username) {
		this.existingUsers.add(username);
	}

	public List<String> getExistingUsers() {
		return Collections.unmodifiableList(existingUsers);
	}

}
