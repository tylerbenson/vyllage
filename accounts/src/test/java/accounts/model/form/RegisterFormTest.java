package accounts.model.form;

import static org.junit.Assert.assertFalse;

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
	public void badEmailFailsTest() {

		RegisterForm form = new RegisterForm();
		form.setFirstName("firstName");
		form.setLastName("lastName");
		form.setEmail("a@");
		form.setPassword("password");

		assertFalse(form.isValid());

		form.setEmail("@b");

		assertFalse(form.isValid());

		form.setEmail("a@b");

		assertFalse(form.isValid());
	}

}
