package util.dateSerialization;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

	private static final String MMM_YYYY_DD = "MMM yyyy dd";

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LocalDateDeserializer.class
			.getName());

	@Override
	public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// logger.info("Parsing date " + jp.getText());

		String dateString = jp.getText();

		if (dateString == null || dateString.isEmpty())
			return null;
		dateString += " 01"; // without the dd part it cannot be parsed
								// into a valid date.

		// logger.info(dateString);

		LocalDate date = LocalDate.parse(dateString,
				DateTimeFormatter.ofPattern(MMM_YYYY_DD));

		return date;
	}

}
