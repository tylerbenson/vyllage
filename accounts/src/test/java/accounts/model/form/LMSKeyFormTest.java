package accounts.model.form;

import org.junit.Assert;
import org.junit.Test;

public class LMSKeyFormTest {

	@Test
	public void testSecretNullIsInvalid() {

		LMSKeyForm form = new LMSKeyForm();

		form.setConsumerKey("aaaa");

		form.setSecret(null);

		Assert.assertTrue(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertTrue(form.secretNotValid());
	}

	@Test
	public void testSecretEmptyIsInvalid() {

		LMSKeyForm form = new LMSKeyForm();

		form.setConsumerKey("aaaa");

		form.setSecret("");

		Assert.assertTrue(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertTrue(form.secretNotValid());
	}

	@Test
	public void testSecretLessThan16IsInvalid() {

		LMSKeyForm form = new LMSKeyForm();

		form.setConsumerKey("aaaa");

		form.setSecret("123456789112345");

		Assert.assertTrue(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertTrue(form.secretNotValid());
	}

	@Test
	public void testConsumerKeyEmptyIsInvalid() {

		LMSKeyForm form = new LMSKeyForm();

		form.setConsumerKey("");

		form.setSecret("1234567891123456");

		Assert.assertTrue(form.isInvalid());
		Assert.assertTrue(form.consumerKeyNotValid());
		Assert.assertFalse(form.secretNotValid());
	}

	@Test
	public void testConsumerKeyNullIsInvalid() {

		LMSKeyForm form = new LMSKeyForm();

		form.setConsumerKey(null);

		form.setSecret("1234567891123456");

		Assert.assertTrue(form.isInvalid());
		Assert.assertTrue(form.consumerKeyNotValid());
		Assert.assertFalse(form.secretNotValid());
	}

	@Test
	public void testIsValid() {

		LMSKeyForm form = new LMSKeyForm();

		form.setConsumerKey("Hello");

		form.setSecret("1234567891123456");

		Assert.assertFalse(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertFalse(form.secretNotValid());
	}

}
