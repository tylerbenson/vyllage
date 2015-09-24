package util.dateSerialization;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DocumentLocalDateTimeDeserializer extends
		JsonDeserializer<LocalDateTime> {

	private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(DocumentLocalDateTimeDeserializer.class.getName());

	@Override
	public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		// logger.info("Parsing date " + jp.getText());

		String dateString = jp.getText();

		if (dateString == null || dateString.isEmpty())
			return null;

		// logger.info(dateString);
		LocalDateTime date = null;
		try {
			date = LocalDateTime.parse(dateString,
					DateTimeFormatter.ofPattern(YYYY_MM_DD));
		} catch (DateTimeParseException e) {
			// date remains null.
		}

		return date;
	}

}
