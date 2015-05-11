package accounts.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import org.junit.Assert;
import org.junit.Test;

import accounts.service.utilities.Encryptor;

public class LinkEncryptorTest {

	@Test
	public void encodeDecode() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException {
		Encryptor enc = new Encryptor("password");
		enc.init();
		String text = "Hello World!";

		String encodedText = enc.encrypt(text);

		Assert.assertEquals(text, enc.decrypt(encodedText));

	}
}
