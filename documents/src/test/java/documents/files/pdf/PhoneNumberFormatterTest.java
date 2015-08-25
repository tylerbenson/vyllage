package documents.files.pdf;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class PhoneNumberFormatterTest {

	private final String FORMATTED_NUMBER = "(555) 555-9999";

	@Test
	public void testPrint() {

		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		String number = "5555559999";

		Assert.assertEquals(FORMATTED_NUMBER,
				formatter.print(number, Locale.US));
	}

	@Test
	public void testPrintPaddedWithZero() {

		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		String number = "95";

		Assert.assertEquals("(000) 000-0095",
				formatter.print(number, Locale.US));
	}

	@Test
	public void testPrintReturnZerosOnEmpty() {

		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		String number = "  ";

		Assert.assertEquals("(000) 000-0000",
				formatter.print(number, Locale.US));
	}

	@Test
	public void testPrintReturnNullOnNull() {

		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		String number = null;

		Assert.assertEquals("(000) 000-0000",
				formatter.print(number, Locale.US));
	}

}
