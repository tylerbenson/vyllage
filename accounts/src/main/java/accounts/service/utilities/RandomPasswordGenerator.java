package accounts.service.utilities;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomPasswordGenerator {

	/**
	 * Generates a random alphanumeric password 20 characters long.
	 *
	 * @return password
	 */
	public String getRandomPassword() {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	/**
	 * Generates a random alphanumeric string of the specified size.
	 *
	 * @param size
	 * @return password
	 */
	public String getRandomString(int size) {
		return RandomStringUtils.randomAlphanumeric(size);
	}

}
