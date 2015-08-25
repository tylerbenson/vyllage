package documents.files.pdf;

import java.util.Locale;

import org.springframework.format.Printer;
import org.springframework.util.StringUtils;

/**
 * Formats a phone number into US locale. <br>
 * ex: (555) 555-9999
 *
 * @author uh
 */
public class PhoneNumberFormatter implements Printer<String> {

	private final String defaultText = "(000) 000-0000";

	@Override
	public String print(String text, Locale locale) {

		if (text == null)
			return defaultText;

		text = StringUtils.trimAllWhitespace(text);

		// 10 because the frontend formatter uses that length, pads with N for
		// missing numbers or ignores numbers after the 10th digit.
		StringBuilder sb = new StringBuilder(10);
		StringBuilder temp = new StringBuilder(text);

		while (temp.length() < 10)
			temp.insert(0, "0");

		char[] chars = temp.toString().toCharArray();

		sb.append("(");
		for (int i = 0; i < chars.length; i++) {
			if (i == 3)
				sb.append(") ");
			else if (i == 6)
				sb.append("-");
			sb.append(chars[i]);
		}

		return sb.toString();
	}

}
