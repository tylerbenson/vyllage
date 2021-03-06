package accounts.model.form;

import org.junit.Assert;
import org.junit.Test;

public class LTIKeyFormTest {

	@Test
	public void testSecretNullIsInvalid() {

		LTIKeyForm form = new LTIKeyForm();

		form.setConsumerKey("aaaa");

		form.setSecret(null);

		Assert.assertTrue(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertTrue(form.secretNotValid());
	}

	@Test
	public void testSecretEmptyIsInvalid() {

		LTIKeyForm form = new LTIKeyForm();

		form.setConsumerKey("aaaa");

		form.setSecret("");

		Assert.assertTrue(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertTrue(form.secretNotValid());
	}

	@Test
	public void testSecretLessThan16IsInvalid() {

		LTIKeyForm form = new LTIKeyForm();

		form.setConsumerKey("aaaa");

		form.setSecret("123456789112345");

		Assert.assertTrue(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertTrue(form.secretNotValid());
	}

	@Test
	public void testConsumerKeyEmptyIsInvalid() {

		LTIKeyForm form = new LTIKeyForm();

		form.setConsumerKey("");

		form.setSecret("1234567891123456");

		Assert.assertTrue(form.isInvalid());
		Assert.assertTrue(form.consumerKeyNotValid());
		Assert.assertFalse(form.secretNotValid());
	}

	@Test
	public void testConsumerKeyNullIsInvalid() {

		LTIKeyForm form = new LTIKeyForm();

		form.setConsumerKey(null);

		form.setSecret("1234567891123456");

		Assert.assertTrue(form.isInvalid());
		Assert.assertTrue(form.consumerKeyNotValid());
		Assert.assertFalse(form.secretNotValid());
	}

	@Test
	public void testIsValid() {

		LTIKeyForm form = new LTIKeyForm();

		form.setConsumerKey("Hello");

		form.setSecret("1234567891123456");

		Assert.assertFalse(form.isInvalid());
		Assert.assertFalse(form.consumerKeyNotValid());
		Assert.assertFalse(form.secretNotValid());
	}

}
