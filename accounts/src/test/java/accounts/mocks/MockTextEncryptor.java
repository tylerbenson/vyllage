package accounts.mocks;

import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Mock encryptor for Solano, since it doesn't support JCE8...
 * 
 * @author uh
 */
public class MockTextEncryptor implements TextEncryptor {

	@Override
	public String encrypt(String text) {
		return text;
	}

	@Override
	public String decrypt(String encryptedText) {
		return encryptedText;
	}

}
