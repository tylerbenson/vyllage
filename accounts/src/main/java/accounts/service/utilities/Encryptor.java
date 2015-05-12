package accounts.service.utilities;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Taken from http://aesencryption.net/
 * 
 */
@Service
public class Encryptor {

	@Value("${link.encoder.key}")
	public String password;

	private static SecretKeySpec secretKey;
	private static byte[] key;
	private MessageDigest sha;

	private Cipher cipher;

	public Encryptor() {
	}

	public Encryptor(String password) {
		this.password = password;
	}

	@PostConstruct
	public void init() throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, UnsupportedEncodingException {
		key = password.getBytes("UTF-8");
		sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); // use only first 128 bit
		secretKey = new SecretKeySpec(key, "AES");
	}

	public String encrypt(String strToEncrypt) {
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getUrlEncoder().encodeToString(
					cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

		} catch (UnsupportedEncodingException | BadPaddingException
				| IllegalBlockSizeException | InvalidKeyException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public String decrypt(String strToDecrypt) {
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getUrlDecoder().decode(
					strToDecrypt)));
		} catch (BadPaddingException | IllegalBlockSizeException
				| InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
}
