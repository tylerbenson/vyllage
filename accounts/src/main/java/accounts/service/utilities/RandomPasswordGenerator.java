package accounts.service.utilities;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomPasswordGenerator {

	public String getRandomPassword() {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	public String getRandomString(int size) {
		return RandomStringUtils.randomAlphanumeric(size);
	}

}
