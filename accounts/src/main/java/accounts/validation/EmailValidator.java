package accounts.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//'Borrowed' from:
//http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/

public class EmailValidator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * Validate email address with regular expression
	 *
	 * @param email
	 *            email for validation
	 * @return true valid email, false invalid email
	 */
	public static boolean isValid(final String email) {
		if (email == null)
			return false;

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();

	}
}
