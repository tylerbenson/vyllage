package documents.files.pdf;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * Searches for * in text and converts them to
 * 
 * @author uh
 *
 */
public class ListFormatter implements Formatter<String> {

	@Override
	public String print(String text, Locale locale) {
		if (text == null || text.isEmpty())
			return text;

		StringBuilder sb = new StringBuilder(text.length());

		if (text.contains("*")) {
			char[] charArray = text.toCharArray();

			for (int i = 0; i < charArray.length; i++) {
				char c = charArray[i];

				if ('*' == c) {
					sb.append("<br/>" + "<strong>-</strong>");
				} else
					sb.append(c);

			}
		} else
			return text;

		return sb.toString();
	}

	@Override
	public String parse(String text, Locale locale) throws ParseException {
		return print(text, locale);
	}

}
