package accounts.model.form;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegisterFormTest {

	@Test
	public void nullFirstNameFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName(null);
		form.setLastName("lastName");
		form.setEmail("email@gmail.com");
		form.setPassword("password");

		assertFalse(form.isValid());
	}

	@Test
	public void emptyFirstNameFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("");
		form.setLastName("lastName");
		form.setEmail("email@gmail.com");
		form.setPassword("password");

		assertFalse(form.isValid());
	}

	@Test
	public void nullLastNameFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName(null);
		form.setEmail("email@gmail.com");
		form.setPassword("password");

		assertFalse(form.isValid());
	}

	@Test
	public void emptyLastNameFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("");
		form.setEmail("email@gmail.com");
		form.setPassword("password");

		assertFalse(form.isValid());
	}

	@Test
	public void nullEmailFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail(null);
		form.setPassword("password");

		assertFalse(form.isValid());
	}

	@Test
	public void nullPasswordFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("email@gmail.com");
		form.setPassword(null);

		assertFalse(form.isValid());
	}

	@Test
	public void emptyPasswordFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("email@gmail.com");
		form.setPassword("");

		assertFalse(form.isValid());
	}

	@Test
	public void passwordLessThanSixCharactersFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("email@gmail.com");
		form.setPassword("12345");

		assertFalse(form.isValid());
	}

	@Test
	public void passwordEqualsSixCharactersFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("email@gmail.com");
		form.setPassword("123456");

		assertTrue(form.isValid());
	}

	@Test
	public void badEmailFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("a@");
		form.setPassword("password");

		assertFalse(form.emailIsValid());

		form.setEmail("@b");

		assertFalse(form.emailIsValid());

		form.setEmail("a@bcd");

		assertFalse(form.emailIsValid());
	}

	@Test
	public void rightEmailPassesTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("a@b.com");
		form.setPassword("password");

		assertTrue(form.emailIsValid());

		form.setEmail("1@sub.acq.org");

		assertTrue(form.emailIsValid());

		form.setEmail("a@b.co");

		assertTrue(form.emailIsValid());
	}
}
