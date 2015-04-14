package accounts.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomPasswordGenerator {

	public String getRandomPassword() {
		return RandomStringUtils.randomAlphanumeric(20);
	}

}
